package com.example.datn.view.Search

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.datn.adapter.productAdapter
import com.example.datn.adapter.productAdapterAll
import com.example.datn.data.dataresult.ProductTypeX
import com.example.datn.data.dataresult.ResponseResult
import com.example.datn.databinding.FragmentSearchTaskBinding
import com.example.datn.repository.repositoryProduct
import com.example.datn.utils.Extension.NumberExtensions.snackBar
import com.example.datn.view.Detail.ProductActivity
import com.example.datn.viewmodel.Products.MainViewModelFactory
import com.example.datn.viewmodel.Products.SearchViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SearchTaskFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentSearchTaskBinding? = null
    private lateinit var viewModel: SearchViewModel
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchTaskBinding.inflate(layoutInflater)
        val repositoryProduct = repositoryProduct()
        val vmFactory = MainViewModelFactory(repositoryProduct)
        viewModel =
            ViewModelProvider(requireActivity(), vmFactory)[SearchViewModel::class.java]
        binding.btnAdd.setOnClickListener {
            val start = binding.txtStart.text.toString().toIntOrNull()
            val end = binding.txtEnd.text.toString().toIntOrNull()
            ListProductSearchActivity.startPrice = start
            ListProductSearchActivity.endPrice = end
            viewModel.getAllProductTypeSearch(ListProductSearchActivity.name,start,end,ListProductSearchActivity.sort)
            dismiss()
        }

        binding.btnDefault.setOnClickListener {
            viewModel.getAllProductTypeSearch(ListProductSearchActivity.name,null,null,ListProductSearchActivity.sort)
            dismiss()
        }

        binding.btn1.setOnClickListener {
            setPrice(0,100000)
        }
        binding.btn2.setOnClickListener {
            setPrice(100000,300000)
        }
        binding.btn3.setOnClickListener {
            setPrice(300000,500000)
        }
        binding.btn4.setOnClickListener {
            setPrice(500000,1000000)
        }
        return binding.root
    }


    private fun setPrice(start : Int? , end : Int?){
        binding.txtStart.setText("$start")
        binding.txtEnd.setText("$end")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
