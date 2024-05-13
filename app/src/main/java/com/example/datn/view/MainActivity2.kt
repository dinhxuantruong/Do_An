package com.example.datn.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.datn.R
import com.example.datn.adapter.OrderViewPageAdapter
import com.example.datn.databinding.ActivityMain2Binding
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity2 : AppCompatActivity() {
    private lateinit var binding : ActivityMain2Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = OrderViewPageAdapter(supportFragmentManager,lifecycle)
        binding.viewPager2.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager2) { tab, position ->
            when (position) {
                0 -> tab.text = "Chờ xác nhận"
                1 -> tab.text = "Đang đóng gói"
                2 -> tab.text = "Đang vận chuyển"
                3 -> tab.text = "Đang giao hàng"
                4 -> tab.text = "Đã nhận"
                else -> tab.text = "Đã hủy"
            }
        }.attach()

    }
}