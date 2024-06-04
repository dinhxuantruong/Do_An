package com.example.datn.view.Orders

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.datn.adapter.adapterListAddress
import com.example.datn.data.dataresult.ResponseResult
import com.example.datn.data.dataresult.apiAddress.Addresse
import com.example.datn.databinding.FragmentListAddressBinding
import com.example.datn.repository.repositoryProduct
import com.example.datn.utils.Extension.NumberExtensions.snackBar
import com.example.datn.viewmodel.Orders.AddressesViewModel
import com.example.datn.viewmodel.Orders.OrdersViewModelFactory


class ListAddressFragment : Fragment() {

private var _binding : FragmentListAddressBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: AddressesViewModel
    private lateinit var listAddress : MutableList<Addresse>
    private var adapter : adapterListAddress? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentListAddressBinding.inflate(inflater,container,false)

        binding.btnAddAddess.setOnClickListener {
           startActivity(Intent(requireActivity(),AddressesActivity::class.java))
        }
        binding.toolListProduct.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        init()
        observeView()


        return  binding.root
    }


    private fun observeView() {
        viewModel.resultAll.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ResponseResult.Success -> {
                    listAddress.clear()
                    val data = result.data.addresses
                    data.forEach { item ->
                        listAddress.add(item)
                    }
                    adapter = adapterListAddress(
                        requireActivity(),
                        object : adapterListAddress.checkBoxOnClick {
                            override fun isCheckedItem(itemAddress: Addresse) {
                                CheckoutFragment.idAddress = itemAddress.id
                                findNavController().popBackStack()
                            }
                        },
                        listAddress
                    )
                    adapter!!.setCheckedById(CheckoutFragment.idAddress!!)
                    binding.recyclerViewCart.adapter = adapter
                }
                is ResponseResult.Error -> {
                    requireActivity().snackBar(result.message)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getAllAddress()
    }

    private fun init() {
        listAddress = mutableListOf()
        binding.recyclerViewCart.setHasFixedSize(true)
        binding.recyclerViewCart.isNestedScrollingEnabled = false
        binding.recyclerViewCart.layoutManager = LinearLayoutManager(requireActivity())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repositoryProduct = repositoryProduct()
        val vmFactory = OrdersViewModelFactory(repositoryProduct)
        viewModel = ViewModelProvider(requireActivity(),vmFactory)[AddressesViewModel::class.java]
        viewModel.getAllAddress()

    }
    override fun onDestroyView() {
        super.onDestroyView()
        adapter = null
        _binding = null
    }


}