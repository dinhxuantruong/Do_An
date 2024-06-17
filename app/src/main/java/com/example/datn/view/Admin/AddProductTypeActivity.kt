package com.example.datn.view.Admin

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.datn.R
import com.example.datn.data.dataresult.ResponseResult
import com.example.datn.databinding.ActivityAddProductTypeBinding
import com.example.datn.repository.repositoryAdmin
import com.example.datn.utils.Extension.NumberExtensions.getFileName
import com.example.datn.utils.Extension.NumberExtensions.snackBar
import com.example.datn.utils.Extension.UploadRequestBody
import com.example.datn.utils.SharePreference.PrefManager
import com.example.datn.viewmodel.Admin.AdminViewModel
import com.example.datn.viewmodel.Admin.AdminViewModelFactory
import com.squareup.picasso.Picasso
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class AddProductTypeActivity : AppCompatActivity() {
    private var _binding: ActivityAddProductTypeBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: AdminViewModel
    private var selectImage: Uri? = null
    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>
    private lateinit var listCate: MutableList<String>
    private var idCate = 0
    private var idCateNew = 0
    private var idType = 0
    private var check = false
    private lateinit var newIdCate: RequestBody

    private var _prefManager: PrefManager? = null
    private val prefManager get() = _prefManager!!
    companion object{
        var checkAddSize = false
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAddProductTypeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        onClick()
        observeView()
    }

    private fun observeView() {
        viewModel.resultType.observe(this@AddProductTypeActivity) {
            when (it) {
                is ResponseResult.Success -> {
                    val productTYpe = it.data.productType
                    Picasso.get().load(productTYpe.image_url).into(binding.imageView)
                    binding.txtName.setText(productTYpe.name)
                    binding.txtTH.setText(productTYpe.trademark)
                    binding.txtHoatChat.setText(productTYpe.active_ingredients)
                    binding.txtCongthuc.setText(productTYpe.recipe)
                    binding.txtMade.setText(productTYpe.made)
                    binding.txtDate.setText(productTYpe.date)
                    binding.txtWeight.setText(productTYpe.weight)
                    binding.txtNguyenLieu.setText(productTYpe.ingredient)
                    binding.txtSize.setText(productTYpe.package_size)
                    binding.txtQuantity.setText(productTYpe.quantity)
                    binding.txtDess.setText(productTYpe.description)
                }

                is ResponseResult.Error -> {

                }
            }
        }
        viewModel.resultCate.observe(this@AddProductTypeActivity) {
            when (it) {
                is ResponseResult.Success -> {
                    listCate.clear()
                    val listCateName = it.data.categories
                    listCateName.forEach { item ->
                        if (item.id == idCateNew && !check) {
                            binding.autoCompleXa.setText(item.name)
                        }
                        listCate.add(item.name)
                    }
                    setAutoCompleteAdapter(binding.autoCompleXa, listCate)
                    binding.autoCompleXa.onItemClickListener =
                        AdapterView.OnItemClickListener { _, _, i, _ ->
                            idCate = it.data.categories[i].id
                            Toast.makeText(this, idCate.toString(), Toast.LENGTH_SHORT).show()
                        }
                }

                is ResponseResult.Error -> {

                }
            }
        }

        viewModel.resultUpdateType.observe(this@AddProductTypeActivity) {
            when (it) {
                is ResponseResult.Success -> {
                    this.snackBar(it.data.message)
                    AdminProductTypeActivity.update = true
                }

                is ResponseResult.Error -> {
                    this.snackBar(it.message)
                }
            }
        }
        viewModel.resultAddType.observe(this@AddProductTypeActivity){
            when(it){
                is ResponseResult.Success -> {
                    val id = it.data.id_type.toInt()
                    intentView(true,id)
                }

                is ResponseResult.Error -> {
                    this.snackBar(it.message)
                }
            }
        }


    }

    private fun setAutoCompleteAdapter(
        autoCompleteTextView: AutoCompleteTextView,
        list: List<String>
    ) {
        val adapter = ArrayAdapter(this, R.layout.list_items, list)
        autoCompleteTextView.setAdapter(adapter)
    }

    private fun onClick() {
        binding.imageView.setOnClickListener {
            chooseImage()
        }

        binding.btnAdd.setOnClickListener {
            uploadImages()
        }

        binding.txtDess.setOnClickListener {
           val intent = Intent(this@AddProductTypeActivity,DescriptionActivity::class.java)
            intent.putExtra("desc",binding.txtDess.text.toString())
            startActivity(intent)
        }
    }

    private fun checkEmpty(
        name: String,
        brand: String,
        activeIngredient: String,
        formula: String,
        madeIn: String,
        expiryDate: String,
        weight: String,
        material: String,
        size: String,
        packaging: String,
        description: String
    ): Boolean {
        return name.isEmpty() || brand.isEmpty() || activeIngredient.isEmpty() || formula.isEmpty() ||
                madeIn.isEmpty() || expiryDate.isEmpty() || weight.isEmpty() || material.isEmpty() ||
                size.isEmpty() || packaging.isEmpty() || description.isEmpty()
    }

    private fun uploadImages() {
        // Lấy các giá trị từ các trường TextInputEditText
        val name = binding.txtName.text.toString()
        val brand = binding.txtTH.text.toString()
        val activeIngredient = binding.txtHoatChat.text.toString()
        val formula = binding.txtCongthuc.text.toString()
        val madeIn = binding.txtMade.text.toString()
        val expiryDate = binding.txtDate.text.toString()
        val weight = binding.txtWeight.text.toString()
        val material = binding.txtNguyenLieu.text.toString()
        val size = binding.txtSize.text.toString()
        val packaging = binding.txtQuantity.text.toString()
        val description = binding.txtDess.text.toString()

        val checkEmpty = checkEmpty(
            name,
            brand,
            activeIngredient,
            formula,
            madeIn,
            expiryDate,
            weight,
            material,
            size,
            packaging,
            description
        )

        if (check) {
            if (checkEmpty || selectImage == null) {
                Toast.makeText(this, "Nhập chưa đủ dữ liệu!", Toast.LENGTH_SHORT).show()
                return
            }
        } else {
            if (checkEmpty) {
                Toast.makeText(this, "Nhập chưa đủ dữ liệu!", Toast.LENGTH_SHORT).show()
                return
            }
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
            imagePart = MultipartBody.Part.createFormData("image_product", file.name, body)
        }

        val newIdType = idType.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
        newIdCate = idCate.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val newIdCateOld = idCateNew.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val newSize = size.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val newName = name.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val newBrand = brand.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val newActiveIngredient =
            activeIngredient.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val newFormula = formula.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val newMadeIn = madeIn.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val newExpiryDate = expiryDate.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val newWeight = weight.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val newMaterial = material.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val newPackaging = packaging.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val newDescription = description.toRequestBody("multipart/form-data".toMediaTypeOrNull())

        if (check) {
            viewModel.addProductType(
                newIdCate,
                newName,
                newActiveIngredient,
                newBrand,
                newFormula,
                newMadeIn,
                newExpiryDate,
                newWeight,
                newMaterial,
                newPackaging,
                newSize,
                newDescription,
                imagePart!!
            )
        } else {
            val idcate : RequestBody = if (idCate <= 0){
                newIdCateOld
            }else{
                newIdCate
            }
            viewModel.updateProductType(
                newIdType, idcate,
                newName,
                newActiveIngredient,
                newBrand,
                newFormula,
                newMadeIn,
                newExpiryDate,
                newWeight,
                newMaterial,
                newPackaging,
                newSize,
                newDescription,
                imagePart
            )
        }
    }


    private fun init() {
        val repository = repositoryAdmin()
        val vmFactory = AdminViewModelFactory(repository)
        viewModel = ViewModelProvider(this, vmFactory)[AdminViewModel::class.java]
        idType = intent.getIntExtra("id", 0)
        check = intent.getBooleanExtra("check", true)
        if (!check) {
            idCateNew = intent.getIntExtra("idCate", 0)
            binding.btnAdd.text = "Cập nhật sản phẩm"
            viewModel.getProductType(idType)
        }else{
            binding.btnAdd.text  = "Thêm sản phẩm"
        }
        _prefManager = PrefManager(this)
        setImageView()
        listCate = mutableListOf()
        viewModel.getAllCategory()
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

    private fun intentView(check: Boolean, idType: Int) {
        val intent = Intent(this, AddProductSizeActivity::class.java)
        intent.putExtra("id", idType)
        intent.putExtra("check", check)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        if (checkAddSize){
            AdminProductTypeActivity.update = true
            finish()
        }
        if (prefManager.getText()!!.isNotEmpty()){
            binding.txtDess.setText(prefManager.getText())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        AdminProductTypeActivity.update = false
        _binding = null
    }
}