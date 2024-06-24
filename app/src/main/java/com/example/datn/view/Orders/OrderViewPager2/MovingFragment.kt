package com.example.datn.view.Orders.OrderViewPager2

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.datn.R
import com.example.datn.adapter.OrderAdapter
import com.example.datn.data.dataresult.ResponseResult
import com.example.datn.data.dataresult.orders.Order
import com.example.datn.databinding.FragmentConfirmBinding
import com.example.datn.databinding.FragmentMovingBinding
import com.example.datn.view.Detail.OrderDetailsActivity
import com.example.datn.viewmodel.Orders.OrdersViewModel


class MovingFragment : Fragment() {

    private var _binding : FragmentMovingBinding? = null
    private val binding get() = _binding!!
    private val viewModel: OrdersViewModel by activityViewModels()
    private var adapter: OrderAdapter? = null
    private lateinit var listPending: MutableList<Order>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentMovingBinding.inflate(inflater,container,false)

        init()
        viewModel.getAllOrderShipping()
        observeView()

        return binding.root
    }

    private fun observeView() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading == true) View.VISIBLE else View.GONE
        }
        viewModel.resultAllOrderShipping.observe(viewLifecycleOwner) { data ->
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
                    adapter = OrderAdapter(requireActivity(), listPending, false,
                        object : OrderAdapter.buttonOnClick {
                            override fun onClick(itemOrder: Order) {
                            }

                            override fun moreOnclick(itemOrder: Order) {
                                val intent =
                                    Intent(requireContext(), OrderDetailsActivity::class.java)
                                intent.putExtra("id", itemOrder.id)
                                intent.putExtra("status","Đang vận chuyển")
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

    override fun onResume() {
        super.onResume()
        viewModel.getAllOrderShipping()
    }
    private fun init() {
        listPending = mutableListOf()
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireActivity())
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