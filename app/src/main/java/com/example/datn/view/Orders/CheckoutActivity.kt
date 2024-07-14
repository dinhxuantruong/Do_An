package com.example.datn.view.Orders

import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
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
import com.example.zalopaykotlin.Api.CreateOrder
import org.json.JSONObject
import vn.zalopay.sdk.Environment
import vn.zalopay.sdk.ZaloPayError
import vn.zalopay.sdk.ZaloPaySDK
import vn.zalopay.sdk.listeners.PayOrderListener
import java.util.UUID

class CheckoutActivity : AppCompatActivity() {
    private var _binding: ActivityCheckoutBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: OrderViewModel
    private var uuid: String = ""
    private var total: Int = 0
    private var finalTotal: Int = 0
    private var oneCall: Boolean = false
    private var discount: Int = 0
    private var freeShip: Int = 0
    private var oldVoucher: String = ""
    private var payment: Boolean? = null
    private var idOrder = 0

    private lateinit var listOrder: MutableList<ItemCartsWithTotal>

    companion object {
        var isLoggedInFirstTime: Boolean = false
        var idAddress: Int? = null
        var voucher: String = ""
    }

    private var adapter: checkoutAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        observeView()

        onClickButton()

        // Thiết lập StrictMode
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        // Khởi tạo ZaloPay SDK
        ZaloPaySDK.init(2553, Environment.SANDBOX)


    }
    private fun onClickButton() {
        binding.bankCheckBox.setOnCheckedChangeListener{_, isChecked ->
            if (isChecked){
                binding.codCheckBox.isChecked = false
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
                    payment = false
                    viewModel.createAddOrders(AddressRequest(CheckoutFragment.idAddress!!.toInt(), 1, 1,uuid, CheckoutFragment.voucher))
                }else if (!binding.codCheckBox.isChecked && binding.bankCheckBox.isChecked) {
                    payment = true
                    viewModel.createAddOrders(AddressRequest(CheckoutFragment.idAddress!!.toInt(), 2, null,uuid, CheckoutFragment.voucher))
                }
            }
            else{
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
                    idOrder = it.data.order.id
                    if (payment == false) {
                        val intent1 = Intent(this@CheckoutActivity, SuccessActivity::class.java)
                        intent1.putExtra("idPay", 0)
                        startActivity(intent1)
                        finish()
                    }else if(payment == true){
                        val total = it.data.order.final_amount
                        val totalString = String.format("%.0f", total)
                        val orderApi = CreateOrder()
                        try {
                            val data: JSONObject? = orderApi.createOrder(totalString)
                            val code = data?.getString("return_code")
                            if (code == "1") {
                                val token = data.getString("zp_trans_token")
                                ZaloPaySDK.getInstance().payOrder(this, token, "demozpdk://app", object : PayOrderListener {
                                    override fun onPaymentSucceeded(p0: String, p1: String, p2: String) {
                                        viewModel.changeStatusZaloPay(idOrder,p0)
                                        viewModel.resultStatusZaloPay.observe(this@CheckoutActivity) {result ->
                                            when (result) {
                                                is ResponseResult.Success -> {
                                                    val intent1 = Intent(this@CheckoutActivity, SuccessActivity::class.java)
                                                    intent1.putExtra("result", "Thanh toán thành công")
                                                    intent1.putExtra("order_id", p0) // Lưu mã hóa đơn
                                                    intent1.putExtra("idPay", 1)
                                                    startActivity(intent1)
                                                    finish()
                                                }

                                                is ResponseResult.Error -> {
                                                    Toast.makeText(this@CheckoutActivity, "Thanh toán không thành công", Toast.LENGTH_SHORT)
                                                        .show()
                                                }
                                            }
                                        }
                                    }

                                    override fun onPaymentCanceled(p0: String, p1: String) {
                                        val intent1 = Intent(this@CheckoutActivity, SuccessActivity::class.java)
                                        intent1.putExtra("result", "Hủy thanh toán")
                                        intent1.putExtra("idPay", 1)
                                        startActivity(intent1)
                                        finish()
                                    }

                                    override fun onPaymentError(zaloPayError: ZaloPayError, p0: String, p1: String) {
                                        val intent1 = Intent(this@CheckoutActivity, SuccessActivity::class.java)
                                        intent1.putExtra("result", "Lỗi thanh toán")
                                        intent1.putExtra("idPay", 1)
                                        startActivity(intent1)
                                        finish()
                                    }
                                })
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
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

    private fun init() {
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

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        ZaloPaySDK.getInstance().onResult(intent)
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