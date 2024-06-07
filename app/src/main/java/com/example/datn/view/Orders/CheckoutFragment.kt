package com.example.datn.view.Orders

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.datn.R
import com.example.datn.adapter.checkoutAdapter
import com.example.datn.data.dataresult.ItemCartsWithTotal
import com.example.datn.data.dataresult.ResponseResult
import com.example.datn.data.dataresult.orders.Order
import com.example.datn.data.dataresult.resultCart
import com.example.datn.data.model.AddressRequest
import com.example.datn.data.model.dataVoucher
import com.example.datn.databinding.FragmentCheckoutBinding
import com.example.datn.utils.Extension.LiveDataExtensions.observeOnce
import com.example.datn.utils.Extension.LiveDataExtensions.observeOnceAfterInit
import com.example.datn.utils.Extension.NumberExtensions.snackBar
import com.example.datn.utils.Extension.NumberExtensions.toVietnameseCurrency
import com.example.datn.view.Detail.CartActivity
import com.example.datn.view.Detail.ProductActivity
import com.example.datn.viewmodel.Products.OrderViewModel
import java.util.UUID

class CheckoutFragment : Fragment() {

    private var _binding : FragmentCheckoutBinding? = null
    private val binding get() = _binding!!

    private val viewModel : OrderViewModel by  activityViewModels()

    private var uuid : String = ""
    private var total : Int = 0
    //private var isLoggedInFirstTime : Boolean = false
    private lateinit var listOrder : MutableList<ItemCartsWithTotal>
    companion object {
        var isLoggedInFirstTime : Boolean = false
        var idAddress: Int? = null
        var voucher : String? = null
    }

    private  var adapter : checkoutAdapter? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCheckoutBinding.inflate(inflater,container,false)
        init()
        viewModel.checkoutOrders()
        if(idAddress == null){
            viewModel.getDefaultAddress()
        }else{
            viewModel.getDetailAddress(idAddress.toString())
        }
        observeView()

        onClickButton()
        return binding.root
    }

    private fun onClickButton() {
        binding.bankCheckBox.setOnCheckedChangeListener{_, isChecked ->
            if (isChecked){
                binding.codCheckBox.isChecked = false
                requireActivity().snackBar("Bank")
            }else{

            }
        }

        binding.btnAllVoucher.setOnClickListener {
            startActivity(Intent(requireActivity(),DiscountActivity::class.java))
        }

        binding.addAddresses.setOnClickListener {
            findNavController().navigate(R.id.action_checkoutFragment_to_listAddressFragment)
        }
        binding.addAddresses2.setOnClickListener {
            startActivity(Intent(requireActivity(),AddressesActivity::class.java))
        }

        binding.codCheckBox.setOnCheckedChangeListener{_, isChecked ->
            if (isChecked){
                binding.bankCheckBox.isChecked = false
                requireActivity().snackBar("Cod")
            }else{

            }
        }

        binding.btnMua.setOnClickListener {
            if (idAddress != null) {
                if (!binding.bankCheckBox.isChecked && !binding.codCheckBox.isChecked ||
                    binding.bankCheckBox.isChecked && binding.codCheckBox.isChecked
                ) {
                    requireActivity().snackBar("Chọn phương thức thanh toán.")
                } else if (binding.codCheckBox.isChecked && !binding.bankCheckBox.isChecked) {
                    viewModel.createAddOrders(AddressRequest(idAddress!!.toInt(), 1, 1,uuid, voucher))
                }
            }else{
                Toast.makeText(requireContext(), "Chưa có địa chỉ", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeView() {
        viewModel.resultDefaultAddress.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ResponseResult.Success -> {
                    binding.addAddresses2.visibility = View.GONE
                    binding.addAddresses.visibility = View.VISIBLE
                    val data = result.data.default_address
                    idAddress = data.id
                    binding.tvName.text = data.username
                    binding.tvSdt.text = " | ${data.phone}"
                    binding.tvAddress.text =
                        "${data.address}, Xã ${data.ward}, Huyện ${data.district}, ${data.province}"
                }

                is ResponseResult.Error -> {
                    binding.addAddresses2.visibility = View.VISIBLE
                    binding.addAddresses.visibility = View.GONE
                }
            }

        }
        viewModel.resultDetailAddress.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ResponseResult.Success -> {
                    val data = result.data.default_address
                    idAddress = data.id
                    binding.tvName.text = data.username
                    binding.tvSdt.text = " | ${data.phone}"
                    binding.tvAddress.text =
                        "${data.address}, Xã ${data.ward}, Huyện ${data.district}, ${data.province}"
                }

                is ResponseResult.Error -> {
                    //
                }
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading == true) View.VISIBLE else View.GONE
        }

        viewModel.resultCreateOrder.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseResult.Success -> {
                    startActivity(Intent(requireActivity(), SuccessActivity::class.java))
                    requireActivity().finish()
                }

                is ResponseResult.Error -> {
                    requireActivity().snackBar(it.message)
                }
            }
        }

        viewModel.resultCheckout.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseResult.Success -> {
                    listOrder.clear()
                    val data = it.data.itemCartsWithTotal
                    data.forEach { item ->
                        listOrder.add(item)
                    }
                    adapter = checkoutAdapter(requireActivity(), listOrder)
                    binding.recyclerViewCart.adapter = adapter
                    total = it.data.total
                    if (voucher == null) {
                        setPriceAll(total, 0, total, false)
                    }
                }

                is ResponseResult.Error -> {
                    requireActivity().snackBar(it.message)
                    binding.btnMua.isEnabled = false
                }
            }
        }
        if (voucher != null){
            viewModel.testVoucher(dataVoucher(total, voucher!!, idAddress))
            if (isLoggedInFirstTime) {
                viewModel.resultCheckVoucher.observeOnceAfterInit(viewLifecycleOwner) { result ->
                    handleResult(result)
                }
            } else {
                viewModel.resultCheckVoucher.observeOnce(viewLifecycleOwner) { result ->
                    handleResult(result)
                }
                isLoggedInFirstTime = true
            }
        }

    }

    private fun setPriceAll(total: Int, discount : Int, final_amount : Int,check : Boolean) {
        binding.total.text = final_amount.toVietnameseCurrency()
        binding.txtCountDraf.text = total.toVietnameseCurrency()
        binding.txtTotalFinal.text = final_amount.toVietnameseCurrency()
        binding.txtMinusVoucher.text = "- ${discount.toVietnameseCurrency()}"
        if (!check){
            binding.txtFreeShip.text = "0 đ"
        }else{
            binding.txtFreeShip.text = "- 30.000 đ"
        }
    }

    private fun init() {
        listOrder = mutableListOf()
        binding.recyclerViewCart.setHasFixedSize(true)
        binding.recyclerViewCart.isNestedScrollingEnabled = false
        binding.recyclerViewCart.layoutManager = LinearLayoutManager(requireContext())

        binding.toolListProduct.setNavigationOnClickListener {
            finishView()
        }
        uuid = UUID.randomUUID().toString()
    }

    override fun onResume() {
        super.onResume()
        if (idAddress==null){
            viewModel.getDefaultAddress()
        }
        if (voucher != null){
            viewModel.testVoucher(dataVoucher(total, voucher!!, idAddress))
            if (isLoggedInFirstTime) {
                viewModel.resultCheckVoucher.observeOnceAfterInit(viewLifecycleOwner) { result ->
                    handleResult(result)
                }
            } else {
                viewModel.resultCheckVoucher.observeOnce(viewLifecycleOwner) { result ->
                    handleResult(result)
                }
                isLoggedInFirstTime = true
            }
        }
    }

    private fun handleResult(result: ResponseResult<Order>) {
        when (result) {
            is ResponseResult.Success -> {
                setPriceAll(
                    total,
                    result.data.discount.toInt(),
                    result.data.final_amount.toInt(),
                    result.data.check
                )
            }

            is ResponseResult.Error -> {
                //
            }
        }
    }


    private fun finishView() {
        startActivity(Intent(requireActivity(), CartActivity::class.java))
        requireActivity().finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        idAddress = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter = null
        _binding = null
    }


}