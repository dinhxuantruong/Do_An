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
import com.example.datn.R
import com.example.datn.adapter.OrderViewPageAdapter
import com.example.datn.adapter.OrderViewPageAdapterAdmin
import com.example.datn.databinding.FragmentListOrderBinding
import com.example.datn.view.Detail.CartActivity
import com.example.datn.viewmodel.Orders.OrdersViewModel


class ListOrderFragment : Fragment() {

    private var _binding : FragmentListOrderBinding? = null
    private val binding get() = _binding!!
    private var adapter: OrderViewPageAdapterAdmin? = null
    private lateinit var viewModel: OrdersViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding=  FragmentListOrderBinding.inflate(inflater,container,false)




        return binding.root
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}