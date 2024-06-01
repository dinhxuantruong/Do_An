package com.example.datn.view.Admin

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.datn.data.dataresult.ResponseResult
import com.example.datn.databinding.ActivityAddProductSizeBinding
import com.example.datn.repository.repositoryAdmin
import com.example.datn.utils.Extension.NumberExtensions.getFileName
import com.example.datn.utils.Extension.NumberExtensions.snackBar
import com.example.datn.utils.Extension.UploadRequestBody
import com.example.datn.viewmodel.Admin.AdminViewModel
import com.example.datn.viewmodel.Admin.AdminViewModelFactory
import com.squareup.picasso.Picasso
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class AddProductSizeActivity : AppCompatActivity() {
    private var _binding: ActivityAddProductSizeBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: AdminViewModel
    private var selectImage: Uri? = null
    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>
    private var idType = 0
    private var idProduct = 0
    private var check = true
    private var image : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAddProductSizeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        observeRequest()
        setOnClick()
    }

    private fun setOnClick() {
        binding.imageView.setOnClickListener {
            chooseImage()
        }
        binding.btnImage.setOnClickListener {
            // Add your image button click logic here
        }
        binding.btnAdd.setOnClickListener {
            uploadImages()
        }
    }

    private fun observeRequest() {
        viewModel.resultAddSize.observe(this) { result ->
            when (result) {
                is ResponseResult.Success -> {
                    this.snackBar(result.data.message)
                    AddProductTypeActivity.checkAddSize = true
                    ListProductFragment.update = false
                    finish()
                }
                is ResponseResult.Error -> {
                    this.snackBar(result.message)
                }
            }
        }
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading == true) View.VISIBLE else View.GONE
        }
        viewModel.resultUpdateSize.observe(this) { result ->
            when (result) {
                is ResponseResult.Success -> {
                    this.snackBar(result.data.message)
                    finish()
                }
                is ResponseResult.Error -> {
                    this.snackBar(result.message)
                }
            }
        }
    }

    private fun init() {
        val repository = repositoryAdmin()
        val vmFactory = AdminViewModelFactory(repository)
        viewModel = ViewModelProvider(this, vmFactory)[AdminViewModel::class.java]
        check  = intent.getBooleanExtra("check",true)
        idProduct = intent.getIntExtra("id", 0)
        idType = intent.getIntExtra("id", 0)
        image = intent.getStringExtra("image")
        if (image != null) {
            Picasso.get().load(image).into(binding.imageView)
        }
        binding.btnAdd.text = if (check) "Thêm" else "Cập nhật"
        if (!check){
            binding.txtSize.setText(intent.getStringExtra("size").toString())
            binding.txtPrice.setText(intent.getStringExtra("price").toString())
            binding.txtStock.setText(intent.getStringExtra("stock").toString())
        }
        setImageView()
    }

    private fun uploadImages() {
        val size = binding.txtSize.text.toString()
        val price = binding.txtPrice.text.toString()
        val stock = binding.txtStock.text.toString()
        if (check && (size.isEmpty() || price.isEmpty() || price.isEmpty() || selectImage == null)) {
            Toast.makeText(this, "Nhập chưa đủ dữ liệu!", Toast.LENGTH_SHORT).show()
            return
        } else if (!check && (size.isEmpty() || price.isEmpty() || price.isEmpty())) {
            Toast.makeText(this, "Nhập chưa đủ dữ liệu!", Toast.LENGTH_SHORT).show()
            return
        }

        var imagePart: MultipartBody.Part? = null
        selectImage?.let {
            val contentProvider = contentResolver
            val parcelFileDescriptor = contentProvider.openFileDescriptor(it, "r", null) ?: return
            val file = File(cacheDir, contentProvider.getFileName(it))
            val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
            val outputStream = FileOutputStream(file)
            inputStream.copyTo(outputStream)
            val body = UploadRequestBody(file, "image")
            imagePart = MultipartBody.Part.createFormData("product_photo", file.name, body)
        }

        val newSize = size.toRequestBody("multipart/from-data".toMediaTypeOrNull())
        val newIdType = idType.toString().toRequestBody("multipart/from-data".toMediaTypeOrNull())
        val newIdProduct = idType.toString().toRequestBody("multipart/from-data".toMediaTypeOrNull())
        val newStock = stock.toRequestBody("multipart/from-data".toMediaTypeOrNull())
        val newPrice = price.toRequestBody("multipart/from-data".toMediaTypeOrNull())

        if (check) {
            viewModel.addProductSize(newSize, newIdType, newStock, newPrice, imagePart!!)
        } else {
            viewModel.upProductSize(newIdProduct, newSize, newStock, newPrice, imagePart)
        }
    }

    private fun chooseImage() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
            val mimeType = arrayOf("image/jpeg", "image/png")
            putExtra(Intent.EXTRA_MIME_TYPES, mimeType)
        }
        imagePickerLauncher.launch(intent)
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

    override fun onDestroy() {
        super.onDestroy()
        AddProductTypeActivity.checkAddSize = false
        ListProductFragment.update = false
        _binding = null
    }
}
