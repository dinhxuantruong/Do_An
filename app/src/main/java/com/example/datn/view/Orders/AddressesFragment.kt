package com.example.datn.view.Orders

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.datn.R
import com.example.datn.databinding.FragmentAddressesBinding


class AddressesFragment : Fragment() {

    private var _binding : FragmentAddressesBinding? = null
    private val binding get() =  _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       _binding = FragmentAddressesBinding.inflate(inflater,container,false)





        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding  = null
    }
}