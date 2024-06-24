package com.example.datn.view.Orders

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.datn.R
import com.example.datn.adapter.adapterListAddress
import com.example.datn.data.dataresult.ResponseResult
import com.example.datn.data.dataresult.apiAddress.Addresse
import com.example.datn.databinding.ActivityListAddressBinding
import com.example.datn.repository.repositoryProduct
import com.example.datn.utils.Extension.NumberExtensions.snackBar
import com.example.datn.viewmodel.Orders.AddressesViewModel
import com.example.datn.viewmodel.Orders.OrdersViewModelFactory

class ListAddressActivity : AppCompatActivity() {
    private var _binding : ActivityListAddressBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: AddressesViewModel
    private lateinit var listAddress : MutableList<Addresse>
    private var adapter : adapterListAddress? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityListAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnAddAddess.setOnClickListener {
            startActivity(Intent(this,AddressesActivity::class.java))
        }
        binding.toolListProduct.setNavigationOnClickListener {
           finish()
        }
        init()
        observeView()


    }
    private fun observeView() {
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading == true) View.VISIBLE else View.GONE
        }
        viewModel.resultAll.observe(this) { result ->
            when (result) {
                is ResponseResult.Success -> {
                    listAddress.clear()
                    val data = result.data.addresses
                    data.forEach { item ->
                        listAddress.add(item)
                    }
                    adapter = adapterListAddress(
                        this,
                        object : adapterListAddress.checkBoxOnClick {
                            override fun isCheckedItem(itemAddress: Addresse) {
                                CheckoutFragment.idAddress = itemAddress.id
                                finish()
                            }
                        },
                        listAddress
                    )
                    adapter!!.setCheckedById(CheckoutFragment.idAddress!!)
                    binding.recyclerViewCart.adapter = adapter
                }
                is ResponseResult.Error -> {
                    this.snackBar(result.message)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getAllAddress()
    }

    private fun init() {
        val repositoryProduct = repositoryProduct()
        val vmFactory = OrdersViewModelFactory(repositoryProduct)
        viewModel = ViewModelProvider(this,vmFactory)[AddressesViewModel::class.java]
        viewModel.getAllAddress()
        listAddress = mutableListOf()
        binding.recyclerViewCart.setHasFixedSize(true)
        binding.recyclerViewCart.isNestedScrollingEnabled = false
        binding.recyclerViewCart.layoutManager = LinearLayoutManager(this)
    }




    override fun onDestroy() {
        super.onDestroy()
        adapter = null
        _binding = null
    }
}