package com.example.datn.view.Admin

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import com.example.datn.data.dataresult.ResponseResult
import com.example.datn.databinding.ActivityAdminProductTypeBinding
import com.example.datn.repository.repositoryAdmin
import com.example.datn.view.Detail.ProductActivity
import com.example.datn.viewmodel.Admin.AdminViewModel
import com.example.datn.viewmodel.Admin.AdminViewModelFactory
import com.velmurugan.paging3android.Adapter.ProductPagerAdapter
import com.velmurugan.paging3android.ProductType
import kotlinx.coroutines.launch

class AdminProductTypeActivity : AppCompatActivity() {
    private var _binding: ActivityAdminProductTypeBinding? = null
    private val binding get() = _binding!!
    private var _adapter: ProductPagerAdapter? = null
    private lateinit var viewModel: AdminViewModel
    private val adapter get() = _adapter!!

    companion object{
        var update = false
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAdminProductTypeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val repository = repositoryAdmin()
        val vmFactory = AdminViewModelFactory(repository)
        viewModel = ViewModelProvider(this, vmFactory)[AdminViewModel::class.java]
        _adapter = ProductPagerAdapter(object : ProductPagerAdapter.ClickListener {

            override fun onClickedItem(itemProduct: ProductType) {
                val intent = Intent(this@AdminProductTypeActivity, ListTypeActivity::class.java)
                intent.putExtra("id", itemProduct.id)
                startActivity(intent)
            }

            override fun onLongItemClick(itemProduct: ProductType) {
                val builder = AlertDialog.Builder(this@AdminProductTypeActivity)
                var text = ""
                text = if (itemProduct.status == 0){
                    "Bán tiếp"
                }else{
                    "Ngừng bán"
                }
                builder.setTitle("Chỉnh sửa")
                builder.setPositiveButton("Sửa") { _, _ ->
                    val idCategory = itemProduct.id_category.toIntOrNull()
                    if (idCategory != null) {
                        intentView(false, itemProduct.id, idCategory)
                    } else {
                        Toast.makeText(this@AdminProductTypeActivity, "ID category is null or invalid", Toast.LENGTH_SHORT).show()
                    }
                }
                builder.setNegativeButton(text) { dialog, _ ->
                    //viewModel.deleteProductType(itemProduct.id)
                    viewModel.changeStatusProduct(itemProduct.id)
                    dialog.dismiss()
                }
                val dialog = builder.create()
                dialog.show()
            }
        })

        binding.toolBarCart.setNavigationOnClickListener {
            finish()
        }

        binding.btnAdd.setOnClickListener {
            intentView(true, null, 1)
        }

        binding.recyclerview.adapter = adapter

        viewModel.errorMessage.observe(this@AdminProductTypeActivity) {
            Toast.makeText(this@AdminProductTypeActivity, it, Toast.LENGTH_SHORT).show()
        }


        adapter.addLoadStateListener { loadState ->
            // show empty list
            if (loadState.refresh is LoadState.Loading || loadState.append is LoadState.Loading) {
                    binding.swipeRefreshLayout.isRefreshing = true
                //binding.progressDialog.isVisible = true
            } else {
                binding.swipeRefreshLayout.isRefreshing = false
                //  binding.progressDialog.isVisible = false
                // If we have an error, show a toast
                val errorState = when {
                    loadState.append is LoadState.Error -> loadState.append as LoadState.Error
                    loadState.prepend is LoadState.Error -> loadState.prepend as LoadState.Error
                    loadState.refresh is LoadState.Error -> loadState.refresh as LoadState.Error
                    else -> null
                }
                errorState?.let {
                    Toast.makeText(this@AdminProductTypeActivity, it.error.toString(), Toast.LENGTH_LONG).show()
                }

            }
        }
        refreshData()
        binding.swipeRefreshLayout.setOnRefreshListener {
            refreshData()
            adapter.addLoadStateListener { loadState ->
                binding.swipeRefreshLayout.isRefreshing =
                    loadState.refresh is LoadState.Loading || loadState.append is LoadState.Loading
            }
        }

        viewModel.resultChangeProduct.observe(this){
            when(it){
                is ResponseResult.Success -> {
                    refreshData()
                }

                is ResponseResult.Error -> {
                    //
                }
            }
        }

    }
    private fun intentView(check: Boolean, idType: Int?, idCate: Int) {
        val intent = Intent(this@AdminProductTypeActivity, AddProductTypeActivity::class.java)
        intent.putExtra("id", idType)
        intent.putExtra("idCate", idCate)
        intent.putExtra("check", check)
        startActivity(intent)
    }
    private fun refreshData() {
        lifecycleScope.launch {
            viewModel.getProductAllType().observe(this@AdminProductTypeActivity) {
                it?.let {
                    adapter.submitData(lifecycle, it)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        //refreshData()
    }

    override fun onDestroy() {
        super.onDestroy()
        _adapter =null
        _binding = null
    }
}
