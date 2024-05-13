package com.example.datn.viewmodel.Orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.datn.repository.repositoryProduct

class OrdersViewModelFactory(private val repositoryProduct: repositoryProduct) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return OrdersViewModel(repositoryProduct) as T
    }
}