package com.example.datn.view.Detail

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.datn.R
import com.example.datn.data.dataresult.ProductTypeX
import com.example.datn.databinding.ActivityListBinding
import com.example.datn.repository.repositoryProduct
import com.example.datn.viewmodel.Products.HomeViewModel
import com.example.datn.viewmodel.Products.MainViewModelFactory
import com.velmurugan.paging3android.Adapter.ProductPagerAdapter
import com.velmurugan.paging3android.ProductType
import kotlinx.coroutines.launch

class ListActivity : AppCompatActivity() {
    lateinit var viewModel: HomeViewModel
    private var _adapter: ProductPagerAdapter? = null
    private val adapter get() = _adapter!!
    private var _binding: ActivityListBinding? = null
    private val binding get() = _binding!!
    private lateinit var listProduct: MutableList<ProductTypeX>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        loadPageDefault()
        binding.recyclerview.adapter = adapter
        binding.toolListProduct.setNavigationOnClickListener {
            onBackPressed()
        }
        binding.radioGroupSort.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioAsc -> {
                    loadPageDesc()
                    binding.recyclerview.scrollToPosition(0)
                }
                R.id.radioDesc -> {
                    loadPageAsc()
                    binding.recyclerview.scrollToPosition(0)
                }
                R.id.radioDefault -> {
                    loadPageDefault()
                    binding.recyclerview.scrollToPosition(0)
                }
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressDialog.visibility = if (isLoading) View.VISIBLE else View.GONE
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
                Toast.makeText(this@ListActivity, it.error.toString(), Toast.LENGTH_LONG).show()
            }
        }

        // Setup the scroll listener for the RecyclerView
        binding.recyclerview.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                // Show or hide the button based on the position
                binding.btnScroll.isVisible = firstVisibleItemPosition > 6
            }
        })

        // Setup the scroll button click listener
        binding.btnScroll.setOnClickListener {
            binding.recyclerview.smoothScrollToPosition(0)
        }
    }

    private fun loadPageDesc() {
        lifecycleScope.launch {
            viewModel.getProductTypeAsc().observe(this@ListActivity) {
                it?.let {
                    adapter.submitData(lifecycle, it)
                }
            }
        }
    }

    private fun loadPageAsc() {
        lifecycleScope.launch {
            viewModel.getProductTypeDesc().observe(this@ListActivity) {
                it?.let {
                    adapter.submitData(lifecycle, it)
                }
            }
        }
    }

    private fun loadPageDefault() {
        lifecycleScope.launch {
            viewModel.getProductTypePage().observe(this@ListActivity) {
                it?.let {
                    adapter.submitData(lifecycle, it)
                }
            }
        }
    }


    private fun init() {
        val repositoryProduct = repositoryProduct()
        val vmFactory = MainViewModelFactory(repositoryProduct)
        viewModel = ViewModelProvider(this, vmFactory)[HomeViewModel::class.java]

        viewModel.errorMessage.observe(this@ListActivity) {
            Toast.makeText(this@ListActivity, it, Toast.LENGTH_SHORT).show()
        }
        listProduct = mutableListOf()
        binding.recyclerview.setHasFixedSize(true)
        _adapter = ProductPagerAdapter(object : ProductPagerAdapter.ClickListener {
            override fun onClickedItem(itemProduct: ProductType) {
                val intent = Intent(this@ListActivity, ProductActivity::class.java)
                intent.putExtra("id", itemProduct.id)
                startActivity(intent)
            }

            override fun onLongItemClick(itemProduct: ProductType) {
                // Handle long item click if needed
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        _adapter = null
        _binding = null
    }
}
