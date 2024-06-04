package com.example.datn.view.Detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.datn.R
import com.example.datn.adapter.OrderAdapter
import com.example.datn.adapter.adapterSatis
import com.example.datn.data.dataresult.OrderItem
import com.example.datn.data.dataresult.ResponseResult
import com.example.datn.data.dataresult.orders.Order
import com.example.datn.databinding.ActivityOrderStatisBinding
import com.example.datn.repository.repositoryProduct
import com.example.datn.utils.Extension.NumberExtensions.toVietnameseCurrency
import com.example.datn.viewmodel.Orders.OrdersViewModel
import com.example.datn.viewmodel.Orders.OrdersViewModelFactory

class OrderStatisActivity : AppCompatActivity() {
    private var _binding : ActivityOrderStatisBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel : OrdersViewModel
    private var adapter: adapterSatis? = null
    private lateinit var listOrder: MutableList<OrderItem>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityOrderStatisBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        obverveView()
        binding.toolBarCart.setNavigationOnClickListener {
            finish()
        }

    }

    private fun obverveView() {
            viewModel.resultOrderReceived.observe(this@OrderStatisActivity) { result ->
                when (result) {
                    is ResponseResult.Success -> {
                         listOrder.clear()
                        val orders = result.data.Orders
                        for (order in orders) {
                            val items = order.items
                            for (item in items) {
                                val price = item.price
                                val quantity = item.quantity
                                val productName = item.product.type.name
                                val orderItem = OrderItem(price, quantity, productName)
                                listOrder.add(orderItem)
                            }
                            adapter = adapterSatis(this@OrderStatisActivity,listOrder)
                            binding.recyclerview.adapter = adapter
                        }
                    }
                    is ResponseResult.Error -> {
                        //
                    }
                }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading == true) View.VISIBLE else View.GONE
        }


        viewModel.resultOrderStatistics.observe(this@OrderStatisActivity){
            when(it) {
                is ResponseResult.Success -> {
                    binding.txtSl.text = "${it.data.deliveredOrdersCount}/${it.data.totalOrdersCount}"
                    binding.txtCount.text = "${it.data.totalDeliveredOrderAmount.toInt().toVietnameseCurrency()}"
                }

                is ResponseResult.Error -> {

                }
            }
        }
    }


    private fun  init(){
        listOrder = mutableListOf()
        binding.recyclerview.setHasFixedSize(true)
        binding.recyclerview.layoutManager = LinearLayoutManager(this)
        val repositoryProduct = repositoryProduct()
        val vmFactory = OrdersViewModelFactory(repositoryProduct)
        viewModel = ViewModelProvider(this@OrderStatisActivity, vmFactory)[OrdersViewModel::class.java]
        viewModel.getOrderStatistics()
        viewModel.getAllOrderReceived()
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}