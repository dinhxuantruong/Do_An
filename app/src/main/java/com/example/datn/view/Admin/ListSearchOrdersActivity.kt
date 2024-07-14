package com.example.datn.view.Admin

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.datn.adapter.OrderAdapterSearch
import com.example.datn.data.dataresult.ResponseResult
import com.example.datn.data.dataresult.orders.Order
import com.example.datn.databinding.ActivityListSearchOrdersBinding
import com.example.datn.repository.repositoryAdmin
import com.example.datn.view.Detail.OrderDetailsActivity
import com.example.datn.viewmodel.Admin.AdminViewModel
import com.example.datn.viewmodel.Admin.AdminViewModelFactory

class ListSearchOrdersActivity : AppCompatActivity() {
    private var _binding: ActivityListSearchOrdersBinding? = null
    private val binding get() = _binding!!
    private var adapter: OrderAdapterSearch? = null
    private lateinit var listPending: MutableList<Order>
    private lateinit var viewModel: AdminViewModel
    private var email = ""
    private var uuid = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityListSearchOrdersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        listPending = mutableListOf()
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        val repository = repositoryAdmin()
        val vmFactory = AdminViewModelFactory(repository)
        viewModel = ViewModelProvider(this, vmFactory)[AdminViewModel::class.java]
        val check = intent.getIntExtra("check", 1)
        if (check == 0) {
            email = intent.getStringExtra("email").toString()
            viewModel.searchOrdersAdmin(email)
        } else if (check == 1) {
            uuid = intent.getStringExtra("uuid").toString()
            Toast.makeText(this, "$uuid", Toast.LENGTH_SHORT).show()
            viewModel.searchUuidOrders(uuid)
        }

        viewModel.resultSearchOrdersUuid.observe(this) {
            when (it) {
                is ResponseResult.Success -> {
                    listPending.clear()
                    val data2 = it.data.Orders
                    data2.forEach { item ->
                        listPending.add(item)
                    }

                    adapter = OrderAdapterSearch(
                        this, listPending, true,
                        object : OrderAdapterSearch.buttonOnClick {
                            override fun onClick(itemOrder: Order) {
                            }

                            override fun moreOnclick(itemOrder: Order) {
                                val intent = Intent(
                                    this@ListSearchOrdersActivity,
                                    OrderDetailsActivity::class.java
                                )
                                intent.putExtra("id", itemOrder.id)
                                intent.putExtra("role", "admin")
                                intent.putExtra("status", "Chờ xác nhận")
                                startActivity(intent)
                            }

                            override fun onRating(itemOrder: Order) {
                                TODO("Not yet implemented")
                            }
                        })
                    binding.recyclerView.adapter = adapter!!
                }

                is ResponseResult.Error -> {
                    //
                }
            }
        }


        viewModel.resultSearchOrders.observe(this) {
            when (it) {
                is ResponseResult.Success -> {
                    listPending.clear()
                    val data2 = it.data.Orders
                    data2.forEach { item ->
                        listPending.add(item)
                    }

                    adapter = OrderAdapterSearch(
                        this, listPending, true,
                        object : OrderAdapterSearch.buttonOnClick {
                            override fun onClick(itemOrder: Order) {
                            }

                            override fun moreOnclick(itemOrder: Order) {
                                val intent = Intent(
                                    this@ListSearchOrdersActivity,
                                    OrderDetailsActivity::class.java
                                )
                                intent.putExtra("id", itemOrder.id)
                                intent.putExtra("role", "admin")
                                intent.putExtra("status", "Chờ xác nhận")
                                startActivity(intent)
                            }

                            override fun onRating(itemOrder: Order) {
                                TODO("Not yet implemented")
                            }
                        })
                    binding.recyclerView.adapter = adapter!!
                }

                is ResponseResult.Error -> {
                    //
                }
            }
        }
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading == true) View.VISIBLE else View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        //viewModel.searchOrdersAdmin(email)
    }


    override fun onDestroy() {
        super.onDestroy()
        adapter = null
        _binding = null
    }
}