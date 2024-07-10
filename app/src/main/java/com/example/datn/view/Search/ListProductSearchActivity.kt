package com.example.datn.view.Search

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.datn.R
import com.example.datn.adapter.productAdapter
import com.example.datn.adapter.productAdapterAll
import com.example.datn.data.dataresult.ProductTypeX
import com.example.datn.data.dataresult.ResponseResult
import com.example.datn.databinding.ActivityListProductSearchBinding
import com.example.datn.repository.repositoryProduct
import com.example.datn.utils.Extension.NumberExtensions.snackBar
import com.example.datn.view.Detail.ProductActivity
import com.example.datn.viewmodel.Products.MainViewModelFactory
import com.example.datn.viewmodel.Products.SearchViewModel

class ListProductSearchActivity : AppCompatActivity() {
    private var _binding: ActivityListProductSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: SearchViewModel
    private lateinit var listProduct: MutableList<ProductTypeX>
    private lateinit var adapter: productAdapterAll

    companion object {
        var startPrice : Int? = null
        var endPrice : Int? = null
        var sort: String? = null
        var name: String = ""
    }
//    override fun onNewIntent(intent: Intent?) {
//        super.onNewIntent(intent)
//        handleIntent(intent) // Xử lý intent khi activity được khởi tạo lại
//    }
//
//    private fun handleIntent(intent: Intent?) {
//        intent?.let {
//            viewModel.getAllProductTypeSearch(name, startPrice, endPrice, sort)
//        }
//    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityListProductSearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        viewModel.getAllProductTypeSearch(name,null,null,null)
        observeView()

        binding.toolBarCart.setNavigationOnClickListener {
            onBackPressed()
        }


        binding.toggleGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.btnASC -> {
                        viewModel.getAllProductTypeSearch(name, startPrice, endPrice,"asc")
                        sort = "asc"
                        binding.btnDESC.isChecked = false
                    }
                    R.id.btnDESC -> {
                        viewModel.getAllProductTypeSearch(name, startPrice, endPrice,"desc")
                        sort = "desc"
                        binding.btnASC.isChecked = false
                    }
                }
            } else {
                if (group.checkedButtonId == View.NO_ID) {
                    sort = null
                    viewModel.getAllProductTypeSearch(name, startPrice, endPrice, null)
                }
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun observeView() {
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading == true) View.VISIBLE else View.GONE
        }

        viewModel.resultSearchProduct.observe(this@ListProductSearchActivity) { result ->
            when (result) {
                is ResponseResult.Success -> {
                    listProduct.clear()
                    val listData = result.data.ProductTypes
                    listData.forEach { item ->
                        listProduct.add(item)
                    }
                    adapter = productAdapterAll(this, object : productAdapter.ClickListener2 {
                        override fun onClickedItem(itemProduct: ProductTypeX) {
                            val intent = Intent(this@ListProductSearchActivity, ProductActivity::class.java)
                            intent.putExtra("id", itemProduct.id)
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

        binding.btnSort.setOnClickListener {
            SearchTaskFragment().show(supportFragmentManager, "New Task Sheet")
        }
    }

    private fun init() {
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
