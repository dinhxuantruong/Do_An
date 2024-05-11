package com.example.datn.view.Orders

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.datn.adapter.checkoutAdapter
import com.example.datn.data.dataresult.ItemCartsWithTotal
import com.example.datn.data.dataresult.ResponseResult
import com.example.datn.data.dataresult.resultCart
import com.example.datn.databinding.FragmentCheckoutBinding
import com.example.datn.utils.Extention.NumberExtensions.snackBar
import com.example.datn.utils.Extention.NumberExtensions.toVietnameseCurrency
import com.example.datn.viewmodel.Products.OrderViewModel

class CheckoutFragment : Fragment() {

    private var _binding : FragmentCheckoutBinding? = null
    private val binding get() = _binding!!

    private val viewModel : OrderViewModel by  activityViewModels()
    private lateinit var listOrder : MutableList<ItemCartsWithTotal>
    private  var adapter : checkoutAdapter? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       _binding = FragmentCheckoutBinding.inflate(inflater,container,false)

        init()
        viewModel.checkoutOrders()
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

        binding.codCheckBox.setOnCheckedChangeListener{_, isChecked ->
            if (isChecked){
                binding.bankCheckBox.isChecked = false
                requireActivity().snackBar("Cod")
            }else{

            }
        }

        binding.btnMua.setOnClickListener {
            if (!binding.bankCheckBox.isChecked && !binding.codCheckBox.isChecked ||
                binding.bankCheckBox.isChecked && binding.codCheckBox.isChecked ){
                requireActivity().snackBar("Chọn phương thức thanh toán.")
            }
        }
    }

    private fun observeView() {

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading == true) View.VISIBLE else View.GONE
        }

        viewModel.resultCheckout.observe(viewLifecycleOwner){
            when(it){
                is ResponseResult.Success -> {
                    listOrder.clear()
                    val data = it.data.itemCartsWithTotal
                    data.forEach { item ->
                        listOrder.add(item)
                    }
                    adapter = checkoutAdapter(requireActivity(),listOrder)
                    binding.recyclerViewCart.adapter = adapter
                    setPriceAll(it.data)
                }

                is ResponseResult.Error -> {
                    requireActivity().snackBar(it.message)
                    binding.btnMua.isEnabled = false
                }
            }
        }
    }

    private fun setPriceAll(data: resultCart) {
        binding.total.text = data.total.toVietnameseCurrency()
        binding.txtCountDraf.text = data.total.toVietnameseCurrency()
        binding.txtTotalFinal.text = data.total.toVietnameseCurrency()
    }

    private fun init(){
        listOrder = mutableListOf()
        binding.recyclerViewCart.setHasFixedSize(true)
        binding.recyclerViewCart.isNestedScrollingEnabled = false
        binding.recyclerViewCart.layoutManager = LinearLayoutManager(requireContext())
    }


    override fun onDestroyView() {
        super.onDestroyView()
        adapter = null
        _binding = null
    }


}