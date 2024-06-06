package com.example.datn.view.Admin

import FirebaseListenerObserver
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.example.datn.R
import com.example.datn.data.dataresult.ResponseResult
import com.example.datn.data.model.Chat
import com.example.datn.databinding.FragmentListProductBinding
import com.example.datn.utils.Extension.NumberExtensions.toVietnameseCurrency
import com.example.datn.utils.SharePreference.PrefManager
import com.example.datn.view.Chat.MessageActivity
import com.example.datn.viewmodel.Admin.AdminViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.velmurugan.paging3android.Adapter.ProductPagerAdapter
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale

class ListProductFragment : Fragment() {

    private var _binding: FragmentListProductBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AdminViewModel by activityViewModels()

    private var _prefManager: PrefManager? = null
    private val prefManager get() = _prefManager!!
    private var _auth: FirebaseAuth? = null
    private val auth get() = _auth!!
    private lateinit var id: String
    private lateinit var reference: DatabaseReference
    private val firebaseListeners = mutableListOf<FirebaseListenerObserver>()
    private var countMessage = 0
    private lateinit var referenceListener: ValueEventListener
    private var currentMonth: Int = 0
    private var lastcurrentMonth: Int = 0
    private var currentYear: Int = 0
    private var currentMonthRevenue = 0
    private var lastMonthRevenue = 0



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
        observeView()

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
                        } else {
                            loginFirebase()
                        }
                        true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)


        binding.btnSearch.setOnClickListener {
            //FirebaseAuth.getInstance().signOut()
        }
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.getStatistic()
        }

        binding.btnAllType.setOnClickListener {
            startActivity(Intent(requireActivity(),AdminProductTypeActivity::class.java))
        }

        return binding.root
    }

    private fun observeView() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.swipeRefreshLayout.isRefreshing = isLoading == true
        }
        viewModel.resultStatistic.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseResult.Success -> {
                    val data = it.data.monthly_revenue
                    binding.txtAllStatis.text =
                        "${it.data.total_revenue_year.toVietnameseCurrency()}"
                    data.forEach { item ->
                        if (item.month == currentMonth) {
                            binding.txtCurrentMonth.text = "Tổng doanh thu T${item.month}"
                            binding.txtM.text =
                                "${item.total_revenue.toInt().toVietnameseCurrency()}"
                            currentMonthRevenue = item.total_revenue.toInt()
                        }
                        if (item.month == lastcurrentMonth) {
                            lastMonthRevenue = item.total_revenue.toInt()
                        }
                    }

                    if (lastcurrentMonth != 0 && lastMonthRevenue != 0) {
                        val revenueChangePercentage = ((currentMonthRevenue - lastMonthRevenue).toDouble() / lastMonthRevenue) * 100
                        val formattedRevenueChangePercentage = String.format("%.1f", revenueChangePercentage)
                        when {
                            revenueChangePercentage > 0 -> {
                                binding.imageUp.visibility = View.VISIBLE
                                binding.imageDown.visibility = View.GONE
                            }
                            revenueChangePercentage < 0 -> {
                                binding.imageUp.visibility = View.GONE
                                binding.imageDown.visibility = View.VISIBLE
                            }
                            else -> {
                                binding.imageUp.visibility = View.GONE
                                binding.imageDown.visibility = View.GONE
                                binding.txtRev.text = "(N/A)"
                            }
                        }
                        binding.txtRev.text = " (${formattedRevenueChangePercentage}%)"
                    } else if (currentMonth != 1) {
                        binding.txtRev.text = " (100%)"
                    } else {
                        binding.txtRev.text = "(N/A)"
                    }

                    binding.txtCountType.text = it.data.total_product_types.toString()
                    binding.txtCountUser.text = it.data.total_users.toString()
                }

                is ResponseResult.Error -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun init() {
        val calendar = Calendar.getInstance()
        currentMonth = calendar.get(Calendar.MONTH) + 1
        lastcurrentMonth = calendar.get(Calendar.MONTH)
        currentYear = calendar.get(Calendar.YEAR)
        _prefManager = PrefManager(requireContext())
        val check = prefManager.isLoginFireBase()
        if (check == true) {
            registerFireBaseChat()
        }
        viewModel.getStatistic()
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


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (activity is AppCompatActivity) {
            val activity = activity as AppCompatActivity
            activity.setSupportActionBar(binding.toolbar)
            activity.supportActionBar?.setDisplayShowTitleEnabled(false)
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
        _binding = null

        // Remove ValueEventListener from reference
        reference.removeEventListener(referenceListener)
    }

}
