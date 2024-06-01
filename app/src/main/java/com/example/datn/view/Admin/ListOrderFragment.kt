package com.example.datn.view.Admin

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import com.example.datn.R
import com.example.datn.adapter.OrderViewPageAdapter
import com.example.datn.adapter.OrderViewPageAdapterAdmin
import com.example.datn.databinding.FragmentListOrderBinding
import com.example.datn.view.Detail.CartActivity
import com.example.datn.viewmodel.Admin.AdminViewModel
import com.example.datn.viewmodel.Orders.OrdersViewModel
import com.google.android.material.tabs.TabLayoutMediator


class ListOrderFragment : Fragment() {

    private var _binding : FragmentListOrderBinding? = null
    private val binding get() = _binding!!
    private var adapter: OrderViewPageAdapterAdmin? = null
    private var tabLayoutMediator: TabLayoutMediator? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding=  FragmentListOrderBinding.inflate(inflater,container,false)

        adapter = OrderViewPageAdapterAdmin(childFragmentManager, lifecycle)
        binding.orderViewPagerAdmin.adapter = adapter

        tabLayoutMediator = TabLayoutMediator(binding.tabLayout, binding.orderViewPagerAdmin) { tab, position ->
            when (position) {
                0 -> tab.text = "Chờ xác nhận"
                1 -> tab.text = "Đang đóng gói"
                2 -> tab.text = "Đang vận chuyển"
                3 -> tab.text = "Đang giao hàng"
                4 -> tab.text = "Đã Hoàn Thành"
                else -> tab.text = "Đã hủy"
            }
        }.also { it.attach() }

        return binding.root
    }

    override fun onDestroyView() {
        tabLayoutMediator?.detach()
        tabLayoutMediator = null
        binding.orderViewPagerAdmin.adapter = null
        adapter = null
        _binding = null
        super.onDestroyView()
    }

}