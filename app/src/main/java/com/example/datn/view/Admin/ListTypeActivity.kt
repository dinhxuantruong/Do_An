package com.example.datn.view.Admin

import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.blogapp.utils.MyButtonClickListener
import com.example.blogapp.utils.MySwipeHelper
import com.example.datn.R
import com.example.datn.adapter.adapterDataType
import com.example.datn.data.dataresult.Product
import com.example.datn.data.dataresult.ResponseResult
import com.example.datn.databinding.ActivityListTypeBinding
import com.example.datn.repository.repositoryAdmin
import com.example.datn.utils.Extension.MyButton
import com.example.datn.utils.Extension.NumberExtensions.setFabIconColor
import com.example.datn.utils.Extension.NumberExtensions.snackBar
import com.example.datn.viewmodel.Admin.AdminViewModel
import com.example.datn.viewmodel.Admin.AdminViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ListTypeActivity : AppCompatActivity() {
    private var _binding : ActivityListTypeBinding? = null
    private val binding get() = _binding!!
    private  var adapter : adapterDataType? = null
    private lateinit var viewModel : AdminViewModel
    private lateinit var listData : MutableList<Product>
    private var id = 0
    private var position = 0
    private var swipe: MySwipeHelper? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityListTypeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()

        observeData()
        setOnclick()
        swipeCart()
    }
    private fun swipeCart() {
        swipe = object : MySwipeHelper(this, binding.recyclerView, 200) {
            override fun instantiateButton(
                viewHolder: RecyclerView.ViewHolder,
                buffer: MutableList<MyButton>
            ) {
                buffer.add(
                    MyButton(this@ListTypeActivity, "Delete", 35, 0, Color.parseColor("#FFFD4945"),
                        object : MyButtonClickListener {
                            override fun onClickedItem(pos: Int) {
                                position = pos
                                viewModel.deProductSize(listData[pos].id)
                            }
                        })
                )
                buffer.add(
                    MyButton(this@ListTypeActivity,"Update",35,0,Color.parseColor("#1EA0DB"),
                        object : MyButtonClickListener{
                            override fun onClickedItem(pos: Int) {
                                val data = listData[pos]
                                intentView(false,data.id,data.image_url,
                                    data.size,data.price.toString(),data.stock.toString())
                            }
                        })
                )
            }
        }
    }
    private fun setOnclick() {
        binding.fab.setOnClickListener {
            intentView(true,id,null,null,null,null)
        }

        binding.toolBar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun intentView(check : Boolean,idType : Int,image : String?,size : String?, price : String?, stock: String?){
        val intent = Intent(this@ListTypeActivity, AddProductSizeActivity::class.java)
        intent.putExtra("id", idType)
        intent.putExtra("check",check)
        intent.putExtra("image",image)
        intent.putExtra("size",size)
        intent.putExtra("price",price)
        intent.putExtra("stock",stock)
        startActivity(intent)
    }

    private fun observeData() {
        viewModel.resultDeleteSize.observe(this@ListTypeActivity){
            when(it){
                is ResponseResult.Success -> {
                    AdminProductTypeActivity.update = true
                    this.snackBar(it.data.message)
                    listData.removeAt(position)
                    if (listData.isEmpty()){
                        AdminProductTypeActivity.update = true
                        finish()
                    }
                    viewModel.getDetailProduct(id)
                    adapter!!.notifyDataSetChanged()
                }

                is ResponseResult.Error -> {
                    this.snackBar(it.message)
                }
            }
        }
        viewModel.resultDetail.observe(this@ListTypeActivity){
            when(it){
                is ResponseResult.Success -> {
                    listData.clear()
                    val data = it.data.ProductType
                    val nameProduct = data.name
                    val quantity = data.quantity
                    val listProduct = data.products
                    listProduct.forEach { item ->
                        listData.add(item)
                    }

                    adapter = adapterDataType(this@ListTypeActivity,listData,nameProduct,quantity)
                    binding.recyclerView.adapter = adapter
                }

                is ResponseResult.Error -> {
                    //
                }
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading == true) View.VISIBLE else View.GONE
        }

    }


    private fun init(){
        listData = mutableListOf()
        val repository = repositoryAdmin()
        val vmFactory = AdminViewModelFactory(repository)
        viewModel = ViewModelProvider(this,vmFactory)[AdminViewModel::class.java]
           id = intent.getIntExtra("id", 0)
        viewModel.getDetailProduct(id)
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        setFabIconColor(this@ListTypeActivity,binding.fab, R.drawable.plusn, R.color.white)
    }

    override fun onResume() {
        super.onResume()
        viewModel.getDetailProduct(id)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}