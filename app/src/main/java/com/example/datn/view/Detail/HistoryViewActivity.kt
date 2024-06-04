package com.example.datn.view.Detail

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import com.example.datn.R
import com.example.datn.databinding.ActivityHistoryViewBinding
import com.example.datn.repository.repositoryProduct
import com.example.datn.view.Admin.ListTypeActivity
import com.example.datn.viewmodel.Products.MainViewModelFactory
import com.example.datn.viewmodel.Products.ViewModelHistory
import com.velmurugan.paging3android.Adapter.ProductPagerAdapter
import com.velmurugan.paging3android.ProductType
import kotlinx.coroutines.launch

class HistoryViewActivity : AppCompatActivity() {
    private var _binding : ActivityHistoryViewBinding? = null
    private val binding get() = _binding!!
     private var _adapter: ProductPagerAdapter? = null
    private val adapter get() = _adapter!!

    private lateinit var viewModel : ViewModelHistory
    private var idCategory : Int = 0
    private var nameCate : String? =null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding  = ActivityHistoryViewBinding.inflate(layoutInflater)
        setContentView(binding.root)


        init()

        binding.toolBarCart.setNavigationOnClickListener {
            finish()
        }
        binding.recyclerviewFavo.adapter = adapter
        viewModel.errorMessage.observe(this@HistoryViewActivity) {
            Toast.makeText(this@HistoryViewActivity, it, Toast.LENGTH_SHORT).show()
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
                Toast.makeText(this@HistoryViewActivity, it.error.toString(), Toast.LENGTH_LONG).show()
            }
        }



    }

    private fun getData(){
        lifecycleScope.launch {
            viewModel.getViewHistory().observe(this@HistoryViewActivity) {
                it?.let {
                    adapter.submitData(lifecycle, it)
                }
            }
        }
    }

    private fun getDataCate(){
        lifecycleScope.launch {
            viewModel.getTypeCategory(idCategory).observe(this@HistoryViewActivity) {
                it?.let {
                    adapter.submitData(lifecycle, it)
                }
            }
        }

    }

    private fun init(){
        idCategory = intent.getIntExtra("idCategory",0)
        nameCate = intent.getStringExtra("name")
        val repositoryProduct = repositoryProduct()
        val vmFactory = MainViewModelFactory(repositoryProduct)
        viewModel = ViewModelProvider(this@HistoryViewActivity,vmFactory)[ViewModelHistory::class.java]
        if (idCategory in 1..6){
            getDataCate()
        }else if (idCategory == 8){
            getAllDataMax()
            binding.toolBarCart.title = "Sản phẩm bán chạy"
        }else if (idCategory == 9){
            getAllDataTime()
            binding.toolBarCart.title = "Sản phẩm mới"
        }
        else{
            getData()
        }
        if (nameCate!=null){
            binding.toolBarCart.title = nameCate
        }
        _adapter = ProductPagerAdapter(object : ProductPagerAdapter.ClickListener {
            override fun onClickedItem(itemProduct: ProductType) {
                val intent = Intent(this@HistoryViewActivity, ProductActivity::class.java)
                intent.putExtra("id", itemProduct.id)
                startActivity(intent)
            }

            override fun onLongItemClick(itemProduct: ProductType) {

            }
        })
    }

    private fun getAllDataMax(){
        lifecycleScope.launch {
            viewModel.getProductTypeMax().observe(this@HistoryViewActivity) {
                it?.let {
                    adapter.submitData(lifecycle, it)
                }
            }
        }
    }

    private fun getAllDataTime(){
        lifecycleScope.launch {
            viewModel.getProductTypeTime().observe(this@HistoryViewActivity) {
                it?.let {
                    adapter.submitData(lifecycle, it)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}