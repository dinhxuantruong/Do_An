package com.example.datn.view.MainView

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.datn.adapter.OrderViewPageAdapter
import com.example.datn.databinding.FragmentOderBinding
import com.example.datn.repository.repositoryProduct
import com.example.datn.view.Orders.OrderViewPager2.ConfirmFragment
import com.example.datn.viewmodel.Orders.OrdersViewModel
import com.example.datn.viewmodel.Orders.OrdersViewModelFactory
import com.google.android.material.tabs.TabLayoutMediator


class OderFragment : Fragment() {

    private var _binding: FragmentOderBinding? = null
    private val binding get() = _binding!!
    private var adapter: OrderViewPageAdapter? = null
    private lateinit var viewModel: OrdersViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOderBinding.inflate(inflater, container, false)
        val repositoryProduct = repositoryProduct()
        val vmFactory = OrdersViewModelFactory(repositoryProduct)
        viewModel = ViewModelProvider(requireActivity(), vmFactory)[OrdersViewModel::class.java]
        Log.e("MAIN","onCreateView222")
      //  adapter = OrderViewPageAdapter(childFragmentManager,viewLifecycleOwner.lifecycle)
        adapter = OrderViewPageAdapter(requireActivity().supportFragmentManager,lifecycle)
        binding.orderViewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.orderViewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "Chờ xác nhận"
                1 -> tab.text = "Đang đóng gói"
                2 -> tab.text = "Đang vận chuyển"
                3 -> tab.text = "Đang giao hàng"
                4 -> tab.text = "Đã nhận"
                else -> tab.text = "Đã hủy"
            }
        }.attach()



        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.orderViewPager.adapter = null
        adapter = null
//        binding.orderViewPager.isSaveEnabled = false
//        binding.orderViewPager.isSaveFromParentEnabled = false
        _binding = null
    }

}