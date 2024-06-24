package com.example.datn.view.Orders

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.datn.R
import com.example.datn.adapter.checkoutAdapter
import com.example.datn.data.dataresult.ItemCartsWithTotal
import com.example.datn.data.dataresult.ResponseResult
import com.example.datn.data.dataresult.orders.Order
import com.example.datn.data.model.AddressRequest
import com.example.datn.data.model.dataVoucher
import com.example.datn.databinding.ActivityCheckoutBinding
import com.example.datn.repository.repositoryProduct
import com.example.datn.utils.Extension.LiveDataExtensions.observeOnce
import com.example.datn.utils.Extension.LiveDataExtensions.observeOnceAfterInit
import com.example.datn.utils.Extension.NumberExtensions.snackBar
import com.example.datn.utils.Extension.NumberExtensions.toVietnameseCurrency
import com.example.datn.view.Detail.CartActivity
import com.example.datn.viewmodel.Products.MainViewModelFactory
import com.example.datn.viewmodel.Products.OrderViewModel
import java.util.UUID

class CheckoutActivity : AppCompatActivity() {
    private var _binding : ActivityCheckoutBinding? = null
    private val binding get () = _binding!!
    private lateinit var viewModel: OrderViewModel
    private var uuid : String = ""
    private var total : Int = 0
    private var finalTotal : Int = 0
    private var oneCall : Boolean = false
    private var discount : Int = 0
    private var freeShip : Int = 0
    private var oldVoucher : String = ""

    private lateinit var listOrder : MutableList<ItemCartsWithTotal>
    companion object {
        var isLoggedInFirstTime : Boolean = false
        var idAddress: Int? = null
        var voucher: String = ""
    }
    private  var adapter : checkoutAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        observeView()

        onClickButton()



    }
    private fun onClickButton() {
        binding.bankCheckBox.setOnCheckedChangeListener{_, isChecked ->
            if (isChecked){
                binding.codCheckBox.isChecked = false
                this.snackBar("Bank")
            }else{

            }
        }

        binding.btnAllVoucher.setOnClickListener {
            startActivity(Intent(this,DiscountActivity::class.java))
        }

        binding.addAddresses.setOnClickListener {
            startActivity(Intent(this,ListAddressActivity::class.java))
        }
        binding.addAddresses2.setOnClickListener {
            startActivity(Intent(this,AddressesActivity::class.java))
        }

        binding.codCheckBox.setOnCheckedChangeListener{_, isChecked ->
            if (isChecked){
                binding.bankCheckBox.isChecked = false
                this.snackBar("Cod")
            }else{

            }
        }

        binding.btnMua.setOnClickListener {
            if (CheckoutFragment.idAddress != null) {
                if (!binding.bankCheckBox.isChecked && !binding.codCheckBox.isChecked ||
                    binding.bankCheckBox.isChecked && binding.codCheckBox.isChecked
                ) {
                    this.snackBar("Chọn phương thức thanh toán.")
                } else if (binding.codCheckBox.isChecked && !binding.bankCheckBox.isChecked) {
                    viewModel.createAddOrders(AddressRequest(CheckoutFragment.idAddress!!.toInt(), 1, 1,uuid, CheckoutFragment.voucher))
                }
            }else{
                Toast.makeText(this, "Chưa có địa chỉ", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun observeView() {
        viewModel.resultDetailAddress.observe(this) { result ->
            when (result) {
                is ResponseResult.Success -> {
                    val data = result.data.default_address
                    binding.addAddresses2.visibility = View.GONE
                    binding.addAddresses.visibility = View.VISIBLE
                    CheckoutFragment.idAddress = result.data.default_address.id
                    if (oneCall) {
                        if (result.data.default_address.province == "Hà Nội") {
                            freeShip = 30000
                            finalTotal = total - discount
                        } else {
                            finalTotal = total + 30000 - discount
                            freeShip = 0
                        }
                        setPriceAll()
                    }
                    binding.tvName.text = data.username
                    binding.tvSdt.text = " | ${data.phone}"
                    if (data.province == "Hà Nội" || data.province == "Hồ Chí Minh") {
                        binding.tvAddress.text =
                            "${data.address}, Phường ${data.ward}, Quận ${data.district}, Thành Phố ${data.province}"
                    } else {
                        binding.tvAddress.text =
                            "${data.address}, Xã ${data.ward}, Huyện ${data.district}, Tỉnh ${data.province}"
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

        viewModel.resultCreateOrder.observe(this) {
            when (it) {
                is ResponseResult.Success -> {
                    startActivity(Intent(this, SuccessActivity::class.java))
                    finish()
                }

                is ResponseResult.Error -> {
                    snackBar(it.message)
                }
            }
        }

        viewModel.resultCheckout.observe(this) {
            when (it) {
                is ResponseResult.Success -> {
                    listOrder.clear()
                    val data = it.data.itemCartsWithTotal
                    data.forEach { item ->
                        listOrder.add(item)
                    }
                    adapter = checkoutAdapter(this, listOrder)
                    binding.recyclerViewCart.adapter = adapter
                    if (!oneCall) {
                        total = it.data.total
                        finalTotal = it.data.final_amount - discount
                        freeShip = if (total == finalTotal){
                            30000
                        }else{
                            0
                        }
                        setPriceAll()
                        oneCall  = true
                    }
                }

                is ResponseResult.Error -> {
                    this.snackBar(it.message)
                    binding.btnMua.isEnabled = false
                }
            }
        }
    }
    private fun setPriceAll() {
        binding.total.text = finalTotal.toVietnameseCurrency()
        binding.txtCountDraf.text = total.toVietnameseCurrency()
        binding.txtTotalFinal.text = finalTotal.toVietnameseCurrency()
        binding.txtMinusVoucher.text = "- ${discount.toVietnameseCurrency()}"
        binding.txtFreeShip.text = "- ${freeShip.toVietnameseCurrency()}"
    }

    private fun init(){
        val repositoryProduct = repositoryProduct()
        val vmFactory = MainViewModelFactory(repositoryProduct)
        viewModel = ViewModelProvider(this, vmFactory)[OrderViewModel::class.java]
        listOrder = mutableListOf()
        binding.recyclerViewCart.setHasFixedSize(true)
        binding.recyclerViewCart.isNestedScrollingEnabled = false
        binding.recyclerViewCart.layoutManager = LinearLayoutManager(this)

        binding.toolListProduct.setNavigationOnClickListener {
            finishView()
        }
        uuid = UUID.randomUUID().toString()
        viewModel.checkoutOrders()
        viewModel.getDetailAddress(CheckoutFragment.idAddress.toString())
    }
    private fun finishView() {
        startActivity(Intent(this, CartActivity::class.java))
        finish()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getDetailAddress(CheckoutFragment.idAddress.toString())
        if (oldVoucher != CheckoutFragment.voucher && oneCall) {
            viewModel.testVoucher(dataVoucher(total, CheckoutFragment.voucher, CheckoutFragment.idAddress))
            if (CheckoutFragment.isLoggedInFirstTime) {
                viewModel.resultCheckVoucher.observeOnceAfterInit(this) { result ->
                    handleResult(result)
                }
            } else {
                viewModel.resultCheckVoucher.observeOnce(this) { result ->
                    handleResult(result)
                }
                CheckoutFragment.isLoggedInFirstTime = true
            }
        }
    }
    private fun handleResult(result: ResponseResult<Order>) {
        when (result) {
            is ResponseResult.Success -> {
                oldVoucher = CheckoutFragment.voucher.ifEmpty {
                    "123"
                }
                discount = result.data.discount.toInt()
                viewModel.getDetailAddress(CheckoutFragment.idAddress.toString())
            }

            is ResponseResult.Error -> {
                discount = 0
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        adapter = null
        CheckoutFragment.voucher = ""
        CheckoutFragment.isLoggedInFirstTime = false
        idAddress = null
        _binding = null
    }
}