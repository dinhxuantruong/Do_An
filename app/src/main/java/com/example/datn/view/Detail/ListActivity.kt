package com.example.datn.view.Detail

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.example.datn.R
import com.example.datn.adapter.productAdapter
import com.example.datn.data.dataresult.ProductTypeX
import com.example.datn.databinding.ActivityListBinding
import com.example.datn.repository.repositoryProduct
import com.example.datn.data.dataresult.ResponseResult
import com.example.datn.viewmodel.Products.HomeViewModel
import com.example.datn.viewmodel.Products.MainViewModelFactory

class ListActivity : AppCompatActivity() {
    lateinit var viewModel: HomeViewModel
    private lateinit var adapter: productAdapter
    private var _binding: ActivityListBinding? = null
    private val binding get() = _binding!!
    private lateinit var listProduct: MutableList<ProductTypeX>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()

        binding.radioGroupSort.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioAsc -> {
                    val sortedList = listProduct.sortedBy { it.price }.toMutableList()
                    setUpRecyclerview(sortedList)
                }
                R.id.radioDesc -> {
                    val sortedList = listProduct.sortedByDescending { it.price }.toMutableList()
                   setUpRecyclerview(sortedList)
                }
                R.id.radioDefault -> {
                   setUpRecyclerview(listProduct)
                }
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressDialog.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.getAllProductType()
        viewModel.resultGetAllPrType.observe(this) {result ->
            when (result) {
                is ResponseResult.Success -> {
                    val data = result.data.ProductTypes
                    data.forEach { item ->
                        listProduct.add(item)
                    }
                    setUpRecyclerview(listProduct)
                }

                is ResponseResult.Error -> {

                }
            }
        }
    }

    private fun init() {
        val repositoryProduct = repositoryProduct()
        val vmFactory = MainViewModelFactory(repositoryProduct)
        viewModel = ViewModelProvider(this, vmFactory)[HomeViewModel::class.java]


        listProduct = mutableListOf()
        binding.recyclerview.setHasFixedSize(true)
    }

    private fun setUpRecyclerview(listProduct : MutableList<ProductTypeX>){
        adapter = productAdapter(this, object : productAdapter.ClickListener2 {
            override fun onClickedItem(itemProduct: ProductTypeX) {
                val intent = Intent(this@ListActivity,ProductActivity::class.java)
                intent.putExtra("id",itemProduct.id)
                startActivity(intent)
            }
        }, listProduct)
        binding.recyclerview.adapter = adapter
        adapter.notifyDataSetChanged()
    }
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}