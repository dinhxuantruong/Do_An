package com.example.datn.viewmodel.Products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.datn.repository.repositoryProduct

class MainViewModelFactory(private val repositoryProduct: repositoryProduct) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(repositoryProduct) as T
            }
            modelClass.isAssignableFrom(ViewModelDetailProduct::class.java) -> {
                ViewModelDetailProduct(repositoryProduct) as T
            }
            modelClass.isAssignableFrom(FavoriteViewModel::class.java) -> {
                FavoriteViewModel(repositoryProduct) as T
            }
            modelClass.isAssignableFrom(CartViewModel::class.java) -> {
                CartViewModel(repositoryProduct) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}