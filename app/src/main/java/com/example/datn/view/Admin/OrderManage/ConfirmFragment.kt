package com.example.datn.view.Admin.OrderManage

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.datn.adapter.OrderAdapter
import com.example.datn.data.dataresult.ResponseResult
import com.example.datn.data.dataresult.ResultMessage
import com.example.datn.data.dataresult.orders.Order
import com.example.datn.databinding.FragmentConfirm2Binding
import com.example.datn.utils.Extension.LiveDataExtensions.observeOnce
import com.example.datn.utils.Extension.LiveDataExtensions.observeOnceAfterInit
import com.example.datn.utils.Extension.NumberExtensions.snackBar
import com.example.datn.view.Detail.OrderDetailsActivity
import com.example.datn.viewmodel.Admin.AdminViewModel


class ConfirmFragment : Fragment() {
    private var _binding: FragmentConfirm2Binding? = null
    private val binding get() = _binding!!
    private val viewModel: AdminViewModel by activityViewModels()
    private var adapter: OrderAdapter? = null
    private lateinit var listPending: MutableList<Order>
    private var isLoggedInFirstTime = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentConfirm2Binding.inflate(inflater, container, false)

        init()
        viewModel.getAllOrderConfirm()
        observeView()


        return binding.root
    }

    private fun observeView() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading == true) View.VISIBLE else View.GONE
        }
        viewModel.resultOrderConfirm.observe(viewLifecycleOwner) { data ->
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
                                val builder = AlertDialog.Builder(requireActivity())
                                builder.setTitle("Xác nhận đơn hàng")
                                builder.setPositiveButton("Xác nhận") { dialog, _ ->
                                    viewModel.confirmOrder(itemOrder.id)
                                    dialog.dismiss()
                                }
                                builder.setNegativeButton("Hủy") { dialog, _ ->
                                    dialog.dismiss()
                                }
                                val dialog = builder.create()
                                dialog.show()
                                if (isLoggedInFirstTime) {
                                    viewModel.resultChangeConfirm.observeOnceAfterInit(
                                        viewLifecycleOwner
                                    ) { result ->
                                        handleLoginResult(result)
                                    }
                                } else {
                                    viewModel.resultChangeConfirm.observeOnce(viewLifecycleOwner) { result ->
                                        handleLoginResult(result)
                                        isLoggedInFirstTime = true
                                    }
                                }
                            }

                            override fun moreOnclick(itemOrder: Order) {
                                val intent =
                                    Intent(requireContext(), OrderDetailsActivity::class.java)
                                intent.putExtra("id", itemOrder.id)
                                intent.putExtra("status","Chờ xác nhận")
                                startActivity(intent)
                            }

                            override fun onRating(itemOrder: Order) {
                                TODO("Not yet implemented")
                            }
                        }, 0
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
        viewModel.getAllOrderConfirm()
    }

    private fun handleLoginResult(dataResult: ResponseResult<ResultMessage>) {
        when (dataResult) {
            is ResponseResult.Success -> {
                requireActivity().snackBar(dataResult.data.message)
                viewModel.getAllOrderConfirm()
            }

            is ResponseResult.Error -> {
                requireActivity().snackBar(dataResult.message)
                viewModel.getAllOrderConfirm()
            }

        }
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
        _binding = null
    }
}