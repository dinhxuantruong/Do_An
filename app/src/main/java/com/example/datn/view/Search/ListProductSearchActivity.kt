package com.example.datn.view.Search

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.example.datn.adapter.productAdapter
import com.example.datn.adapter.productAdapterAll
import com.example.datn.data.dataresult.ProductTypeX
import com.example.datn.data.dataresult.ResponseResult
import com.example.datn.databinding.ActivityListProductSearchBinding
import com.example.datn.databinding.ActivityListSearchBinding
import com.example.datn.repository.repositoryProduct
import com.example.datn.utils.Extension.NumberExtensions.snackBar
import com.example.datn.view.Detail.ProductActivity
import com.example.datn.viewmodel.Products.HomeViewModel
import com.example.datn.viewmodel.Products.MainViewModelFactory
import com.example.datn.viewmodel.Products.SearchViewModel

class ListProductSearchActivity : AppCompatActivity() {
    private var _binding : ActivityListProductSearchBinding? = null
    private val binding get() =  _binding!!
    private lateinit var viewModel : SearchViewModel
    private var name : String = ""
    private lateinit var listProduct: MutableList<ProductTypeX>
    private lateinit var adapter : productAdapterAll
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityListProductSearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        viewModel.getAllProductTypeSearch(name)
        observeView()
        binding.toolBarCart.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun observeView() {
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading == true) View.VISIBLE else View.GONE
        }

        viewModel.resultSearchProduct.observe(this@ListProductSearchActivity){result ->
            when(result){
                is ResponseResult.Success -> {
                    listProduct.clear()
                    val listData = result.data.ProductTypes
                    listData.forEach { item ->
                        listProduct.add(item)
                    }
                    adapter = productAdapterAll(this, object : productAdapter.ClickListener2 {
                        override fun onClickedItem(itemProduct: ProductTypeX) {
                           val intent = Intent(this@ListProductSearchActivity,ProductActivity::class.java)
                            intent.putExtra("id",itemProduct.id)
                            startActivity(intent)
                        }
                    }, listProduct)
                    binding.recyclerViewCart.adapter = adapter

                }
                is ResponseResult.Error -> {
                    this.snackBar(result.message)
                }
            }
        }
    }

    private fun init(){
        val repositoryProduct = repositoryProduct()
        val vmFactory = MainViewModelFactory(repositoryProduct)
        viewModel = ViewModelProvider(this, vmFactory)[SearchViewModel::class.java]
        listProduct = mutableListOf()
        binding.recyclerViewCart.setHasFixedSize(true)
         name = intent.getStringExtra("name").toString()
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}