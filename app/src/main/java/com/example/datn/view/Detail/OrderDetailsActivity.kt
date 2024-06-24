package com.example.datn.view.Detail

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.datn.adapter.adapterOrderDetails
import com.example.datn.data.dataresult.Item
import com.example.datn.data.dataresult.ResponseResult
import com.example.datn.databinding.ActivityOrderDetailsBinding
import com.example.datn.repository.repositoryProduct
import com.example.datn.utils.Extension.NumberExtensions.toVietnameseCurrency
import com.example.datn.viewmodel.Products.MainViewModelFactory
import com.example.datn.viewmodel.Products.OrderViewModel

class OrderDetailsActivity : AppCompatActivity() {
    private var _binding: ActivityOrderDetailsBinding? = null
    private val binding get() = _binding!!

    private var id = 0
    private var status = ""
    private lateinit var viewModel: OrderViewModel
    private lateinit var listProduct: MutableList<Item>
    private var adapter: adapterOrderDetails? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityOrderDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        observeView()

    }

    private fun observeView() {

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading == true) View.VISIBLE else View.GONE
        }
        viewModel.resultDetailsOrder.observe(this@OrderDetailsActivity) {
            when (it) {
                is ResponseResult.Success -> {
                    val dataUser = it.data
                    val finalTotal =dataUser.final_amount.toInt()
                    val drafTotal = dataUser.total.toInt()
                    val discount  = dataUser.discount.toInt()

                    binding.tvName.text = dataUser.name
                    binding.tvSdt.text = " | ${dataUser.phone}"
                    binding.tvAddress.text = "${dataUser.address}, Xã ${dataUser.ward}, Huyện ${dataUser.district}, Tỉnh ${dataUser.province}"
                    binding.txtThanhToan.text = dataUser.payment_method
                    binding.txtTotalFinal.text = "${finalTotal.toVietnameseCurrency()}"
                    binding.txtCreateAt.text = dataUser.created_at
                    binding.txtUuid.text =dataUser.uuid
                    binding.txtCountDraf.text = "${drafTotal.toVietnameseCurrency()}"
                    binding.txtMinusVoucher.text = "- ${discount.toVietnameseCurrency()}"
                    if (drafTotal - (finalTotal + discount) == 0){
                        binding.txtFreeShip.text = "- 30000 đ"
                    }else{
                        binding.txtFreeShip.text = "- 0 đ"
                    }

                    listProduct.clear()
                    val data = dataUser.items
                    data.forEach { item ->
                        listProduct.add(item)
                    }
                    val uuid = dataUser.uuid
                    adapter = adapterOrderDetails(this@OrderDetailsActivity, listProduct)
                    binding.recyclerViewCart.adapter = adapter
                }

                is ResponseResult.Error -> {
                    Toast.makeText(this@OrderDetailsActivity, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun init() {
        listProduct = mutableListOf()
        id = intent.getIntExtra("id", 0)
        status = intent.getStringExtra("status").toString()
        binding.btnXacNhan.text = status
        val repositoryProduct = repositoryProduct()
        val vmFactory = MainViewModelFactory(repositoryProduct)
        viewModel =
            ViewModelProvider(this@OrderDetailsActivity, vmFactory)[OrderViewModel::class.java]
        viewModel.getDetailsOrder(id)

        binding.recyclerViewCart.setHasFixedSize(true)
        binding.recyclerViewCart.isNestedScrollingEnabled = false
        binding.recyclerViewCart.layoutManager = LinearLayoutManager(this)

        binding.toolListProduct.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}