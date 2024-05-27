package com.example.datn.view.Admin.OrderManage

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
import com.example.datn.databinding.FragmentCancelBinding
import com.example.datn.databinding.FragmentCancelledBinding
import com.example.datn.viewmodel.Admin.AdminViewModel


class CancelFragment : Fragment() {

    private var _binding : FragmentCancelBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AdminViewModel by activityViewModels()
    private var adapter: OrderAdapter? = null
    private lateinit var listOrder: MutableList<Order>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCancelBinding.inflate(inflater,container,false)




        init()
        viewModel.getAllOrderCancel()
        observeView()

        return binding.root
    }

    private fun observeView() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading == true) View.VISIBLE else View.GONE
        }
        viewModel.resultAllOrderCancel.observe(viewLifecycleOwner) { data ->
            when (data) {
                is ResponseResult.Success -> {
                    listOrder.clear()
                    val data2 = data.data.Orders
                    data2.forEach { item ->
                        listOrder.add(item)
                    }
                    if (listOrder.size == 0) {
                        visiGoneView()
                    } else {
                        visiView()
                    }
                    adapter = OrderAdapter(requireActivity(), listOrder, true,
                        object : OrderAdapter.buttonOnClick {
                            override fun onClick(itemOrder: Order) {
                            }

                            override fun moreOnclick(itemOrder: Order) {

                            }
                        }, 5
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
        viewModel.getAllOrderCancel()
    }
    private fun init() {
        listOrder = mutableListOf()
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