package com.example.datn.view.Orders

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.datn.adapter.adapterVoucher
import com.example.datn.data.dataresult.Coupon
import com.example.datn.data.dataresult.ResponseResult
import com.example.datn.data.model.dataVoucher
import com.example.datn.databinding.ActivityDiscountBinding
import com.example.datn.repository.repositoryProduct
import com.example.datn.utils.Extension.NumberExtensions.snackBar
import com.example.datn.viewmodel.Orders.OrdersViewModelFactory
import com.example.datn.viewmodel.Orders.VoucherViewModel

class DiscountActivity : AppCompatActivity() {
    private var _binding: ActivityDiscountBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: VoucherViewModel
    private var _adapter: adapterVoucher? = null
    private val adapter get() = _adapter!!
    private lateinit var listVoucher: MutableList<Coupon>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDiscountBinding.inflate(layoutInflater)
        setContentView(binding.root)


        init()
        observeData()
        binding.btnSubmit.setOnClickListener {
            CheckoutFragment.voucher = binding.txtVoucher.text.toString()
            finish()
        }

    }

    private fun init() {
        listVoucher = mutableListOf()
        val repositoryProduct = repositoryProduct()
        viewModel = ViewModelProvider(
            this,
            OrdersViewModelFactory(repositoryProduct)
        )[VoucherViewModel::class.java]
        viewModel.getAllVoucher()

        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun observeData() {
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading == true) View.VISIBLE else View.GONE
        }

        viewModel.resultAllVoucher.observe(this) {
            when (it) {
                is ResponseResult.Success -> {
                    listVoucher.clear()
                    val data = it.data.coupons
                    data.forEach { item ->
                        listVoucher.add(item)
                    }
                    _adapter = adapterVoucher(this,listVoucher,object : adapterVoucher.onClickVoucher{
                        override fun onClick(voucher: Coupon) {
                            CheckoutFragment.voucher = voucher.code
                            finish()
                        }
                    })
                    binding.recyclerView.adapter = adapter
                }

                is ResponseResult.Error -> {
                    this.snackBar(it.message)
                }
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _adapter = null
        _binding = null
    }
}