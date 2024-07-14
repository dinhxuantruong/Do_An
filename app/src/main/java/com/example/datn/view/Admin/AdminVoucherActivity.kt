package com.example.datn.view.Admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.datn.R
import com.example.datn.adapter.adapterVoucher
import com.example.datn.data.dataresult.Coupon
import com.example.datn.data.dataresult.ResponseResult
import com.example.datn.databinding.ActivityAdminVoucherBinding
import com.example.datn.repository.repositoryAdmin
import com.example.datn.view.Orders.CheckoutFragment
import com.example.datn.viewmodel.Admin.AdminViewModel
import com.example.datn.viewmodel.Admin.AdminViewModelFactory

class AdminVoucherActivity : AppCompatActivity() {
    private  var _binding : ActivityAdminVoucherBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel : AdminViewModel
    private var _adapter: adapterVoucher? = null
    private val adapter get() = _adapter!!
    private lateinit var listVoucher: MutableList<Coupon>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAdminVoucherBinding.inflate(layoutInflater)
        setContentView(binding.root)


        init()
        obseverView()
        binding.btnAdd.setOnClickListener {
            startActivity(Intent(this,AddVoucherActivity::class.java))
        }
        //binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.getAllVoucher()
        //}





    }

    private fun obseverView() {
        viewModel.resultAllVoucher.observe(this@AdminVoucherActivity){
            when(it){
                is ResponseResult.Success -> {
                    listVoucher.clear()
                    val data = it.data.coupons
                    data.forEach { item ->
                        listVoucher.add(item)
                    }
                    _adapter = adapterVoucher(this,listVoucher,object : adapterVoucher.onClickVoucher{
                        override fun onClick(voucher: Coupon) {

                        }
                    })
                    binding.recyclerview.adapter = adapter
                }

                is ResponseResult.Error -> {
                    Toast.makeText(this@AdminVoucherActivity, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun init(){
        listVoucher = mutableListOf()
        //binding.recyclerview.setHasFixedSize(true)
        binding.recyclerview.layoutManager = LinearLayoutManager(this)
        val repository = repositoryAdmin()
        val vmFactory = AdminViewModelFactory(repository)
        viewModel = ViewModelProvider(this,vmFactory)[AdminViewModel::class.java]
        viewModel.getAllVoucher()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getAllVoucher()
    }

    override fun onDestroy() {
        super.onDestroy()
        _adapter = null
        _binding = null
    }
}