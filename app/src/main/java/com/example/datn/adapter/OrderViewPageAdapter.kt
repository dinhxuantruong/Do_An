package com.example.datn.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.datn.view.Orders.OrderViewPager2.CancelledFragment
import com.example.datn.view.Orders.OrderViewPager2.ConfirmFragment
import com.example.datn.view.Orders.OrderViewPager2.DeliveringFragment
import com.example.datn.view.Orders.OrderViewPager2.MovingFragment
import com.example.datn.view.Orders.OrderViewPager2.PackageFragment
import com.example.datn.view.Orders.OrderViewPager2.ReceivedFragment

class OrderViewPageAdapter (fragment: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragment, lifecycle) {
    override fun getItemCount(): Int {
        return 6
    }

    override fun createFragment(position: Int): Fragment {

        return when (position) {
            0 -> ConfirmFragment() //Chờ xác nhận
            1 -> PackageFragment() //đang đóng gói
            2 -> MovingFragment() //đang vận chuyển
            3 -> DeliveringFragment() //Đang giao hàng
            4 -> ReceivedFragment() //Đã nhận được hàng
            else -> CancelledFragment() //Đã hủ
        }
    }
}