package com.example.datn.view.Admin.OrderManage

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.datn.R
import com.example.datn.adapter.OrderAdapter
import com.example.datn.data.dataresult.ResponseResult
import com.example.datn.data.dataresult.orders.Order
import com.example.datn.databinding.FragmentDeliveringBinding
import com.example.datn.view.Detail.OrderDetailsActivity
import com.example.datn.viewmodel.Admin.AdminViewModel

class DeliFragment : Fragment() {

    private var _binding : FragmentDeliveringBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AdminViewModel by activityViewModels()
    private var adapter: OrderAdapter? = null
    private lateinit var listPending: MutableList<Order>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDeliveringBinding.inflate(inflater,container,false)



        init()
        viewModel.getAllOrderDelivery()
        observeView()

        return binding.root
    }

    private fun observeView() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading == true) View.VISIBLE else View.GONE
        }
        viewModel.resultAllOrderDelivery.observe(viewLifecycleOwner) { data ->
            when (data) {
                is ResponseResult.Success -> {
                    listPending.clear()
                    val data2 = data.data.Orders
                    data2.forEach { item ->
                        listPending.add(item)
                    }
                    if (listPending.size == 0) {
                        visiGoneView()
                    } else {
                        visiView()
                    }
                    adapter = OrderAdapter(requireActivity(), listPending, true,
                        object : OrderAdapter.buttonOnClick {
                            override fun onClick(itemOrder: Order) {
                            }

                            override fun moreOnclick(itemOrder: Order) {
                                val intent =
                                    Intent(requireContext(), OrderDetailsActivity::class.java)
                                intent.putExtra("id", itemOrder.id)
                                intent.putExtra("status","Đang giao hàng")
                                startActivity(intent)
                            }

                            override fun onRating(itemOrder: Order) {
                                TODO("Not yet implemented")
                            }
                        }, 3
                    )
                    binding.recyclerView.adapter = adapter!!
                }

                is ResponseResult.Error -> {
                    //
                }


            }
        }

    }

    private fun init() {
        listPending = mutableListOf()
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireActivity())
    }

    override fun onResume() {
        super.onResume()
        viewModel.getAllOrderDelivery()
    }
    private fun visiGoneView() {
        binding.apply {
            txtEmpty.visibility = View.VISIBLE
            imageView6.visibility = View.VISIBLE
        }
    }

    private fun visiView() {
        binding.apply {
            txtEmpty.visibility = View.GONE
            imageView6.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter = null
        _binding = null
    }
}