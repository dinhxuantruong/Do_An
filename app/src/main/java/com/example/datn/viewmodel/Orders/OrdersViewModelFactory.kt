package com.example.datn.viewmodel.Orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.datn.repository.repositoryProduct
import com.example.datn.viewmodel.Products.CartViewModel
import com.example.datn.viewmodel.Products.FavoriteViewModel
import com.example.datn.viewmodel.Products.HomeViewModel
import com.example.datn.viewmodel.Products.OrderViewModel
import com.example.datn.viewmodel.Products.ViewModelDetailProduct

class OrdersViewModelFactory(private val repositoryProduct: repositoryProduct) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(OrdersViewModel::class.java) -> {
                OrdersViewModel(repositoryProduct) as T
            }  modelClass.isAssignableFrom(AddressesViewModel::class.java) -> {
                AddressesViewModel(repositoryProduct) as T
            } modelClass.isAssignableFrom(RatingViewModel::class.java) -> {
                RatingViewModel(repositoryProduct) as T
            }modelClass.isAssignableFrom(VoucherViewModel::class.java) -> {
                VoucherViewModel(repositoryProduct) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}