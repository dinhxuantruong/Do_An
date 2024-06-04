package com.example.datn.view.Detail

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.datn.adapter.adapterRateHistory
import com.example.datn.data.dataresult.ResponseResult
import com.example.datn.data.dataresult.ReviewRate
import com.example.datn.databinding.ActivityHistoryRateBinding
import com.example.datn.repository.repositoryProduct
import com.example.datn.viewmodel.Products.MainViewModelFactory
import com.example.datn.viewmodel.Products.ViewModelHistory

class HistoryRateActivity : AppCompatActivity() {
    private var _binding: ActivityHistoryRateBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ViewModelHistory
    private lateinit var adapter: adapterRateHistory
    private lateinit var listRating: MutableList<ReviewRate>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityHistoryRateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        callViewModel()
        observeData()
        binding.toolBarCart.setNavigationOnClickListener {
            onBackPressed()
        }

    }

    private fun observeData() {
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading == true) View.VISIBLE else View.GONE
        }
        viewModel.resultHistoryRating.observe(this@HistoryRateActivity) {
            when (it) {
                is ResponseResult.Success -> {
                    listRating.clear()
                    val data = it.data.reviews
                    data.forEach { item ->
                        listRating.add(item)
                    }
                    adapter = adapterRateHistory(this, listRating, it.data.name, it.data.image_url)
                    binding.recyclerview.adapter = adapter
                }

                is ResponseResult.Error -> {

                }
            }
        }

    }

    private fun callViewModel() {
        viewModel.getHistoryRating()
    }

    private fun init() {
        val repositoryProduct = repositoryProduct()
        val vmFactory = MainViewModelFactory(repositoryProduct)
        viewModel =
            ViewModelProvider(this@HistoryRateActivity, vmFactory)[ViewModelHistory::class.java]
        listRating = mutableListOf()
        binding.recyclerview.setHasFixedSize(true)
        binding.recyclerview.layoutManager = LinearLayoutManager(this)

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}