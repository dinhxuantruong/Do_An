package com.example.datn.view.Orders.OrderViewPager2

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.datn.R
import com.example.datn.adapter.OrderAdapter
import com.example.datn.data.dataresult.ResponseResult
import com.example.datn.data.dataresult.orders.Order
import com.example.datn.databinding.FragmentCancelledBinding
import com.example.datn.databinding.FragmentConfirmBinding
import com.example.datn.repository.repositoryProduct
import com.example.datn.viewmodel.Orders.OrdersViewModel
import com.example.datn.viewmodel.Orders.OrdersViewModelFactory


class ConfirmFragment : Fragment() {

    private var _binding : FragmentConfirmBinding? = null
    private val binding get() = _binding!!
    private val viewModel : OrdersViewModel by activityViewModels()
    private var adapter : OrderAdapter? = null
    private lateinit var listPending : MutableList<Order>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentConfirmBinding.inflate(inflater,container,false)


        init()
        observeView()

 viewModel.getOrderPending()


        return binding.root
    }

    private fun init(){
        listPending = mutableListOf()
        binding.recyclerViewCart.setHasFixedSize(true)
        binding.recyclerViewCart.layoutManager = LinearLayoutManager(requireActivity())
    }

    private fun observeView() {
        viewModel.resultOrderPending.observe(viewLifecycleOwner, Observer { data ->
            when(data){
                is ResponseResult.Success -> {
                    listPending.clear()
                    val data2 = data.data.Orders
                    data2.forEach { item ->
                        listPending.add(item)
                    }
                    adapter = OrderAdapter(requireActivity(),listPending)
                    binding.recyclerViewCart.adapter = adapter!!
                }

                is ResponseResult.Error -> {
                    //
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}