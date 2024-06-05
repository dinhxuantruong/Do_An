package com.example.datn.view.Admin

import FirebaseListenerObserver
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import com.example.datn.R
import com.example.datn.data.dataresult.ResponseResult
import com.example.datn.data.model.Chat
import com.example.datn.databinding.FragmentListProductBinding
import com.example.datn.utils.Extension.NumberExtensions.snackBar
import com.example.datn.utils.SharePreference.PrefManager
import com.example.datn.view.Chat.MessageActivity
import com.example.datn.viewmodel.Admin.AdminViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.velmurugan.paging3android.Adapter.ProductPagerAdapter
import com.velmurugan.paging3android.ProductType
import kotlinx.coroutines.launch
import java.util.Locale

class ListProductFragment : Fragment() {

    private var _binding: FragmentListProductBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AdminViewModel by activityViewModels()

    private var _adapter: ProductPagerAdapter? = null
    private val adapter get() = _adapter!!
    private var _prefManager: PrefManager? = null
    private val prefManager get() = _prefManager!!
    private var _auth: FirebaseAuth? = null
    private val auth get() = _auth!!
    private lateinit var id: String
    private lateinit var reference: DatabaseReference
    private val firebaseListeners = mutableListOf<FirebaseListenerObserver>()
    private var countMessage = 0
    private lateinit var referenceListener:  ValueEventListener

    companion object {
        var update = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _auth = FirebaseAuth.getInstance()
        reference = FirebaseDatabase.getInstance().reference.child("Chats")
        referenceListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var newCountMessage = 0
                for (dataSnap in snapshot.children) {
                    val chat = dataSnap.getValue(Chat::class.java)
                    if (chat?.receiver == _auth!!.uid && chat?.isseen == false) {
                        newCountMessage += 1
                    }
                }
                if (newCountMessage != countMessage) {
                    countMessage = newCountMessage
                    requireActivity().invalidateOptionsMenu()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error if needed
            }
        }

        reference.addValueEventListener(referenceListener)
        val observer = FirebaseListenerObserver(reference, referenceListener)
        firebaseListeners.add(observer)
        lifecycle.addObserver(observer)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentListProductBinding.inflate(inflater, container, false)

        init()
        _adapter = ProductPagerAdapter(object : ProductPagerAdapter.ClickListener {
            override fun onClickedItem(itemProduct: ProductType) {
                val intent = Intent(requireActivity(), ListTypeActivity::class.java)
                intent.putExtra("id", itemProduct.id)
                startActivity(intent)
            }

            override fun onLongItemClick(itemProduct: ProductType) {
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Chỉnh sửa")
                builder.setPositiveButton("Sửa") { _, _ ->
                    val idCategory = itemProduct.id_category?.toIntOrNull()
                    if (idCategory != null) {
                        intentView(false, itemProduct.id, idCategory)
                    } else {
                        Toast.makeText(context, "ID category is null or invalid", Toast.LENGTH_SHORT).show()
                    }
                }
                builder.setNegativeButton("Xóa") { dialog, _ ->
                    viewModel.deleteProductType(itemProduct.id)
                    dialog.dismiss()
                }
                val dialog = builder.create()
                dialog.show()
            }
        })

        setViewData()
        viewModel.resultDeleteType.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseResult.Success -> {
                    //requireActivity().snackBar(it.data.message)
                    setViewData()
                }

                is ResponseResult.Error -> {
                   // requireActivity().snackBar(it.message)
                }
            }
        }
        binding.recyclerviewFavo.adapter = adapter
        viewModel.errorMessage.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        }

        adapter.addLoadStateListener { loadState ->
            binding.progressDialog.isVisible = loadState.refresh is LoadState.Loading || loadState.append is LoadState.Loading
            val errorState = when {
                loadState.append is LoadState.Error -> loadState.append as LoadState.Error
                loadState.prepend is LoadState.Error -> loadState.prepend as LoadState.Error
                loadState.refresh is LoadState.Error -> loadState.refresh as LoadState.Error
                else -> null
            }
            errorState?.let {
                Toast.makeText(requireContext(), it.error.toString(), Toast.LENGTH_LONG).show()
            }
        }

//         Set up MenuProvider for handling menu
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.list_menu, menu)
                val menuItem = menu.findItem(R.id.btnMessage)
                val actionView = menuItem.actionView
                val chatIcon = actionView?.findViewById<TextView>(R.id.txtChatView)
                chatIcon?.let {
                    it.text = if (countMessage > 99) "99" else countMessage.toString()
                    it.visibility = if (countMessage == 0) View.GONE else View.VISIBLE
                    actionView.setOnClickListener {
                        onMenuItemSelected(menuItem)
                    }
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.btnMessage -> {
                        val currentUser = auth.currentUser
                        if (currentUser != null) {
                            startActivity(Intent(requireActivity(), MessageActivity::class.java))
                        }else {
                            loginFirebase()
                        }
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        binding.btnAdd.setOnClickListener {
            intentView(true, null, 1)
        }

        binding.btnSearch.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
        }
        return binding.root
    }

    private fun init() {
        _prefManager = PrefManager(requireContext())
        val check = prefManager.isLoginFireBase()
        if (check == true) {
            registerFireBaseChat()
        }

    }

    private fun loginFirebase() {
        val email = prefManager.getEmail()!!
        val password = prefManager.getEmail()!!
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    startActivity(Intent(requireActivity(), MessageActivity::class.java))
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Incorrect account or password",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun registerFireBaseChat() {
        val email = prefManager.getEmail()!!
        val password = prefManager.getEmail()!!
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { it ->
            if (it.isSuccessful) {
                prefManager.setLoginFireBase(false)
                Toast.makeText(
                    requireContext(),
                    "Register is successfully",
                    Toast.LENGTH_SHORT
                ).show()
                id = auth.currentUser!!.uid
                val refUsers = FirebaseDatabase.getInstance().reference.child("Users").child(id)
                val userHashMap = HashMap<String, Any>()
                userHashMap["uid"] = id
                userHashMap["username"] = email
                val url = prefManager.getUrl()
                userHashMap["profile"] = url ?: "https://picsum.photos/200"
                userHashMap["status"] = "offline"
                userHashMap["search"] = email.lowercase(Locale.ROOT)
                refUsers.updateChildren(userHashMap).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            requireContext(),
                            "Register update is successful",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    private fun intentView(check: Boolean, idType: Int?, idCate: Int) {
        val intent = Intent(requireActivity(), AddProductTypeActivity::class.java)
        intent.putExtra("id", idType)
        intent.putExtra("idCate", idCate)
        intent.putExtra("check", check)
        startActivity(intent)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (activity is AppCompatActivity) {
            val activity = activity as AppCompatActivity
            activity.setSupportActionBar(binding.toolbar)
            activity.supportActionBar?.setDisplayShowTitleEnabled(false)
        }
    }

    private fun setViewData() {
        lifecycleScope.launch {
            viewModel.getProductFavorite().observe(viewLifecycleOwner) {
                it?.let {
                    adapter.submitData(lifecycle, it)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (update) {
            setViewData()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        firebaseListeners.forEach { lifecycle.removeObserver(it) }
        firebaseListeners.clear()
        _auth = null
        _prefManager = null
        if (activity is AppCompatActivity) {
            val activity = activity as AppCompatActivity
            activity.setSupportActionBar(null)
        }
        update = false
        _adapter = null
        _binding = null

        // Remove ValueEventListener from reference
        reference.removeEventListener(referenceListener)
    }

}
