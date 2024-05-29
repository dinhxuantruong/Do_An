package com.example.datn.view.MainView

import FirebaseListenerObserver
import android.app.Activity
import android.app.Dialog
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.datn.R
import com.example.datn.data.dataresult.ResponseResult
import com.example.datn.data.dataresult.ResultMessage
import com.example.datn.databinding.FragmentUserBinding
import com.example.datn.repository.repositoryProduct
import com.example.datn.utils.Extension.LiveDataExtensions.observeOnce
import com.example.datn.utils.Extension.LiveDataExtensions.observeOnceAfterInit
import com.example.datn.utils.Extension.NumberExtensions.getFileName
import com.example.datn.utils.Extension.NumberExtensions.snackBar
import com.example.datn.utils.Extension.UploadRequestBody
import com.example.datn.utils.SharePreference.PrefManager
import com.example.datn.view.Auth.AuthActivity
import com.example.datn.view.Detail.OrderStatisActivity
import com.example.datn.viewmodel.Products.MainViewModelFactory
import com.example.datn.viewmodel.Products.OrderViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import com.squareup.picasso.Picasso
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream


class UserFragment : Fragment() {

    private var _binding: FragmentUserBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: OrderViewModel
    private var _prefManager: PrefManager? = null
    private val prefManager get() = _prefManager!!
    private val firebaseListeners = mutableListOf<FirebaseListenerObserver>()
    private var selectImage: Uri? = null
    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>
    private var isLoggedInFirstTime = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUserBinding.inflate(layoutInflater)

        init()
        setImageView()
        onClickButton()
        observeView()

        return binding.root
    }

    private fun onClickButton() {
        binding.btnLogout.setOnClickListener {
            logout()
        }
        binding.btnChooseImage.setOnClickListener {
            chooseImage()
        }
        binding.btnSave.setOnClickListener {
            uploadImages("","","")
        }

        binding.btnTk.setOnClickListener {
            startActivity(Intent(requireActivity(),OrderStatisActivity::class.java))
        }

        binding.btnProfile.setOnClickListener {
            val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.custom_dialog, null)

            // AlertDialog Builder
            val builder = AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setTitle("Update Profile")

            val alertDialog = builder.show()
            val txtName = dialogView.findViewById<EditText>(R.id.txtName)
            val txtSdt = dialogView.findViewById<EditText>(R.id.txtSdt)
            val txtDiachi = dialogView.findViewById<EditText>(R.id.txtDiachi)
            val btnSubmit = dialogView.findViewById<Button>(R.id.btnSubmit)


            btnSubmit.setOnClickListener {
                val name = txtName.text.toString()
                val phone = txtSdt.text.toString()
                val address = txtDiachi.text.toString()
                uploadImages(name , phone , address )
                alertDialog.dismiss()
            }
        }
    }

    private fun setImageView() {
        imagePickerLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = result.data
                    selectImage = data?.data
                    binding.imageView.setImageURI(selectImage)
                }
            }
    }

    private fun chooseImage() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            //Định dạng kiểu file
            type = "image/*"
            val mimeType = arrayOf("image/jpeg", "image/png")
            putExtra(Intent.EXTRA_MIME_TYPES, mimeType)
        }
        imagePickerLauncher.launch(intent)
    }

    private fun observeView() {
        viewModel.resultProfile.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseResult.Success -> {
                    Picasso.get().load(it.data.profile_photo.toString()).into(binding.imageView)
                    if (it.data.phone.toString().isEmpty()) {
                        binding.txtPhone.visibility = View.GONE
                    } else {
                        binding.txtPhone.text = it.data.phone.toString()
                        binding.txtPhone.visibility = View.VISIBLE
                    }
                    binding.name.text = it.data.name
                }

                is ResponseResult.Error -> {
                    //
                }
            }
        }

        if (isLoggedInFirstTime) {
            viewModel.resultUpdateProfile.observeOnceAfterInit(viewLifecycleOwner) { result ->
                handleLoginResult(result)
            }
        } else {
            viewModel.resultUpdateProfile.observeOnce(viewLifecycleOwner) { result ->
                handleLoginResult(result)
                isLoggedInFirstTime = true
            }
        }



        viewModel.resultLogout.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseResult.Success -> {
                    FirebaseAuth.getInstance().signOut()
                    prefManager.removeDate()
                    stopFirebaseNotification()
                    removeFirebaseListeners()
                    startActivity(Intent(requireActivity(), AuthActivity::class.java))
                    requireActivity().finish()
                }

                is ResponseResult.Error -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun handleLoginResult(result: ResponseResult<ResultMessage>) {
        when(result){
            is ResponseResult.Success -> {
               viewModel.getProfileUser()
            }

            is ResponseResult.Error -> {

            }
        }
    }

    private fun init() {
        _prefManager = PrefManager(requireContext())
        val repositoryProduct = repositoryProduct()
        val vmFactory = MainViewModelFactory(repositoryProduct)
        viewModel = ViewModelProvider(requireActivity(), vmFactory)[OrderViewModel::class.java]
        viewModel.getProfileUser()
        // Example of adding a listener
        val reference = FirebaseDatabase.getInstance().reference.child("SomeChild")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Handle data change
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        }
        reference.addValueEventListener(listener)
        firebaseListeners.add(FirebaseListenerObserver(reference, listener))
    }

    private fun logout() {
        viewModel.logout()
    }

    private fun uploadImages(name : String?, phone : String?, address : String?) {
        var imagePart: MultipartBody.Part? = null
        selectImage?.let {
            val contentProvider = requireActivity().contentResolver
            val parcelFileDescriptor = contentProvider.openFileDescriptor(it, "r", null) ?: return
            val file = File(requireContext().cacheDir, contentProvider.getFileName(it))
            val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
            val outputStream = FileOutputStream(file)
            inputStream.copyTo(outputStream)
            val body = UploadRequestBody(file, "image")
            imagePart = MultipartBody.Part.createFormData("profile_photo", file.name, body)
        }
        val a = name!!.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val b = phone!!.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val c = address!!.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        viewModel.updateProFileUser(a,c,b,imagePart)

    }

    private fun removeFirebaseListeners() {
        for (observer in firebaseListeners) {
            observer.onDestroy(viewLifecycleOwner)
        }
        firebaseListeners.clear()
    }

    private fun stopFirebaseNotification() {
        FirebaseMessaging.getInstance().unsubscribeFromTopic("your_topic_name")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("Notification", "Unsubscribed successfully")
                } else {
                    Log.e("Notification", "Failed to unsubscribe")
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _prefManager = null
        _binding = null
    }
}