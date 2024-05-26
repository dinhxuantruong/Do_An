package com.example.datn.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.datn.view.Admin.OrderManage.AccomplishedFragment
import com.example.datn.view.Admin.OrderManage.BeingTransFragment
import com.example.datn.view.Admin.OrderManage.CancelFragment
import com.example.datn.view.Admin.OrderManage.PackingFragment
import com.example.datn.view.Orders.OrderViewPager2.CancelledFragment
import com.example.datn.view.Orders.OrderViewPager2.ConfirmFragment
import com.example.datn.view.Orders.OrderViewPager2.DeliveringFragment
import com.example.datn.view.Orders.OrderViewPager2.MovingFragment
import com.example.datn.view.Orders.OrderViewPager2.PackageFragment
import com.example.datn.view.Orders.OrderViewPager2.ReceivedFragment

class OrderViewPageAdapterAdmin (fragment: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragment, lifecycle) {
    override fun getItemCount(): Int {
        return 6
    }

    override fun createFragment(position: Int): Fragment {

        return when (position) {
            0 -> com.example.datn.view.Admin.OrderManage.ConfirmFragment() //Chờ xác nhận
            1 -> PackingFragment() //đang đóng gói
            2 -> BeingTransFragment() //đang vận chuyển
            3 -> DeliveringFragment() //Đang giao hàng
            4 -> AccomplishedFragment() //Đã nhận được hàng
            else -> CancelFragment() //Đã hủ
        }
    }
}