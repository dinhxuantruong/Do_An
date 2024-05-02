package com.example.datn.view.Detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import com.example.datn.R
import com.example.datn.databinding.ActivityListBinding
import com.example.datn.repository.repositoryProduct
import com.example.datn.viewmodel.Products.HomeViewModel
import com.example.datn.viewmodel.Products.MainViewModelFactory
import com.velmurugan.paging3android.Adapter.ProductPagerAdapter
import com.velmurugan.paging3android.ProductType
import kotlinx.coroutines.launch

class ListActivity : AppCompatActivity() {
    lateinit var viewModel: HomeViewModel
    private  var  _adapter : ProductPagerAdapter? = null
    private val adapter get() = _adapter!!
    private  var _binding : ActivityListBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val repositoryProduct = repositoryProduct()
        val vmFactory  = MainViewModelFactory(repositoryProduct)
        viewModel = ViewModelProvider(this,vmFactory)[HomeViewModel::class.java]



        _adapter = ProductPagerAdapter(object : ProductPagerAdapter.ClickListener{

            override fun onClickedItem(itemBlog: ProductType) {
                Toast.makeText(this@ListActivity,itemBlog.id.toString(), Toast.LENGTH_SHORT).show()
            }
        })
        // Sử dụng RetrofitClient để lấy instance của RetrofitService
        //  val retrofitService = RetrofitClient.retrofitService
        binding.recyclerview.adapter = adapter



        viewModel.errorMessage.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }

        adapter.addLoadStateListener { loadState ->
            // show empty list
            if (loadState.refresh is LoadState.Loading ||
                loadState.append is LoadState.Loading)
                binding.progressDialog.isVisible = true
            else {
                binding.progressDialog.isVisible = false
                // If we have an error, show a toast
                val errorState = when {
                    loadState.append is LoadState.Error -> loadState.append as LoadState.Error
                    loadState.prepend is LoadState.Error ->  loadState.prepend as LoadState.Error
                    loadState.refresh is LoadState.Error -> loadState.refresh as LoadState.Error
                    else -> null
                }
                errorState?.let {
                    Toast.makeText(this, it.error.toString(), Toast.LENGTH_LONG).show()
                }

            }
        }

        // Sử dụng lifecycleScope để chạy coroutine và thu thập dữ liệu
        lifecycleScope.launch {
            viewModel.getMovieList2().observe(this@ListActivity) {
                it?.let {
                    adapter!!.submitData(lifecycle, it)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _adapter = null
    }
}