package com.example.datn.view.Orders

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.datn.databinding.ActivityOrdersBinding
import com.example.datn.repository.repositoryProduct
import com.example.datn.viewmodel.Products.MainViewModelFactory
import com.example.datn.viewmodel.Products.OrderViewModel

class OrdersActivity : AppCompatActivity() {
    private var _binding: ActivityOrdersBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: OrderViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityOrdersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repositoryProduct = repositoryProduct()
        val vmFactory = MainViewModelFactory(repositoryProduct)
        viewModel = ViewModelProvider(this@OrdersActivity, vmFactory)[OrderViewModel::class.java]

    }
//    override fun onBackPressed() {
//        super.onBackPressed()
//        startActivity(Intent(this,OrdersActivity::class.java))
//        finish()
//    }
    override fun onDestroy() {
        super.onDestroy()
    _binding = null
    }
}