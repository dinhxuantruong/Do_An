package com.example.datn.view.Detail

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.datn.R
import com.example.datn.adapter.adapterOrderDetails
import com.example.datn.data.dataresult.Item
import com.example.datn.data.dataresult.ResponseResult
import com.example.datn.databinding.ActivityOrderDetailsBinding
import com.example.datn.repository.repositoryProduct
import com.example.datn.utils.Extension.NumberExtensions.snackBar
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
    private var role: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityOrderDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        observeView()

        onLongClick()

    }


    private fun onLongClick(){
        binding.txtUuid.setOnLongClickListener {
            // Get the text to copy
            val textToCopy = binding.txtUuid.text.toString()

            // Get the ClipboardManager
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

            // Create a new ClipData
            val clip = ClipData.newPlainText("Copied Text", textToCopy)

            // Set the ClipData to the ClipboardManager
            clipboard.setPrimaryClip(clip)

            // Show a toast message
            Toast.makeText(this, "Đã sao chép", Toast.LENGTH_SHORT).show()

            true // Return true to indicate the event was handled
        }
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

                    when(it.data.status){
                        0 -> {
                            if (role == "admin") {
                                binding.btnXacNhan.setTextColor(ContextCompat.getColor(this, R.color.white))
                                binding.btnXacNhan.setBackgroundResource(R.color.colorPrimary)
                                binding.btnXacNhan.text = "Hủy đơn hàng"
                                binding.btnXacNhan.setOnClickListener {
                                    viewModel.deleteOrderAdmin(id)
                                }
                            } else {
                                binding.btnXacNhan.isEnabled = false
                                binding.btnXacNhan.text = "Chờ xác nhận"
                            }
                        }
                        1 -> {  binding.btnXacNhan.text = "Đang đóng gói"}
                        2 -> {  binding.btnXacNhan.text = "Đang vận chuyển"}
                        3 -> {  binding.btnXacNhan.text = "Đang giao hàng"}
                        4 -> {  binding.btnXacNhan.text = "Đã hoàn thành"}
                        5 -> {  binding.btnXacNhan.text = "Đã hủy"}
                    }

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

        viewModel.resultOrderDelete.observe(this@OrderDetailsActivity){
            when(it){
                is ResponseResult.Success -> {
                    Toast.makeText(this, it.data.message, Toast.LENGTH_SHORT).show()
                    finish()
                }

                is ResponseResult.Error -> {
                    this.snackBar(it.message)
                }
            }
        }
    }


    private fun init() {
        listProduct = mutableListOf()
        id = intent.getIntExtra("id", 0)
        status = intent.getStringExtra("status").toString()
        role = intent.getStringExtra("role").toString()
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