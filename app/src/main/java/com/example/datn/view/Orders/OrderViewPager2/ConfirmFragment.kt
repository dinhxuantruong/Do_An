package com.example.datn.view.Orders.OrderViewPager2

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.datn.adapter.OrderAdapter
import com.example.datn.data.dataresult.ResponseResult
import com.example.datn.data.dataresult.ResultMessage
import com.example.datn.data.dataresult.orders.Order
import com.example.datn.databinding.FragmentConfirmBinding
import com.example.datn.utils.Extension.LiveDataExtensions.observeOnce
import com.example.datn.utils.Extension.LiveDataExtensions.observeOnceAfterInit
import com.example.datn.utils.Extension.NumberExtensions.snackBar
import com.example.datn.view.Detail.OrderDetailsActivity
import com.example.datn.viewmodel.Orders.OrdersViewModel


class ConfirmFragment : Fragment() {

    private var _binding : FragmentConfirmBinding? = null
    private val binding get() = _binding!!
    private val viewModel : OrdersViewModel by activityViewModels()
    private var adapter : OrderAdapter? = null
    private lateinit var listPending : MutableList<Order>
    private var isLoggedInFirstTime  =false
    private var check = 1
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentConfirmBinding.inflate(inflater,container,false)


        init()
        observeView()

 viewModel.getOrderPending()

        binding.swipeRefreshLayout.setOnRefreshListener {
            check = 0
            viewModel.getOrderPending()
        }

        return binding.root
    }

    private fun init(){
        listPending = mutableListOf()
        binding.recyclerViewCart.setHasFixedSize(true)
        binding.recyclerViewCart.layoutManager = LinearLayoutManager(requireActivity())
    }

    private fun observeView() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading){
                if (check == 0) {
                    binding.swipeRefreshLayout.isRefreshing = true
                    check = 1
                }else{
                    binding.progressBar.visibility = View.VISIBLE
                }
            }else{
                binding.swipeRefreshLayout.isRefreshing = false
                binding.progressBar.visibility = View.GONE
            }
        }
        viewModel.resultOrderPending.observe(viewLifecycleOwner) { data ->
            when (data) {
                is ResponseResult.Success -> {
                    listPending.clear()
                    val data2 = data.data.Orders
                    data2.forEach { item ->
                        listPending.add(item)
                    }
                    if (listPending.size == 0) {
                        visiGoneView()
                    }else{
                        visiView()
                    }
                    adapter = OrderAdapter(requireActivity(), listPending, false,
                        object : OrderAdapter.buttonOnClick {
                            override fun onClick(itemOrder: Order) {
                                viewModel.deleteOrder(itemOrder.id)
                                if (isLoggedInFirstTime) {
                                    viewModel.resultDeleteOrder.observeOnceAfterInit(
                                        viewLifecycleOwner
                                    ) { result ->
                                        handleLoginResult(result)
                                    }
                                } else {
                                    viewModel.resultDeleteOrder.observeOnce(viewLifecycleOwner) { result ->
                                        handleLoginResult(result)
                                        isLoggedInFirstTime = true
                                    }
                                }
                            }

                            override fun moreOnclick(itemOrder: Order) {
                                val intent =
                                    Intent(requireContext(), OrderDetailsActivity::class.java)
                                intent.putExtra("id", itemOrder.id)
                                startActivity(intent)
                            }

                            override fun onRating(itemOrder: Order) {
                                TODO("Not yet implemented")
                            }
                        }, 0
                    )
                    binding.recyclerViewCart.adapter = adapter!!
                }

                is ResponseResult.Error -> {
                    viewModel.getOrderPending()
                }

            }
        }
    }

    private fun handleLoginResult(dataResult: ResponseResult<ResultMessage>) {
        when (dataResult) {
            is ResponseResult.Success -> {
                visiView()
                viewModel.getOrderPending()
                requireActivity().snackBar(dataResult.data.message)
                viewModel.getOrderPending()
            }

            is ResponseResult.Error -> {
                requireActivity().snackBar(dataResult.message)
                viewModel.getOrderPending()
            }


        }
    }

    private fun visiView() {
        binding.apply {
            txtEmpty.visibility = View.GONE
            imageView6.visibility = View.GONE
        }
    }

    private fun visiGoneView() {
        binding.apply {
            txtEmpty.visibility = View.VISIBLE
            imageView6.visibility = View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getOrderPending()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter = null
        _binding = null
    }

}