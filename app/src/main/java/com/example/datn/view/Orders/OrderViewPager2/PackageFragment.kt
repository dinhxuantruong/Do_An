package com.example.datn.view.Orders.OrderViewPager2

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.datn.R
import com.example.datn.databinding.FragmentConfirmBinding
import com.example.datn.databinding.FragmentPackageBinding
import com.example.datn.databinding.FragmentPaymentsBinding


class PackageFragment : Fragment() {

    private var _binding : FragmentPackageBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentPackageBinding.inflate(inflater,container,false)

        Log.e("MAIN","onCreateView223332")
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}