package com.example.datn.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.datn.repository.repositoryAuth

class MainViewModelFactory(private val repositoryAuth: repositoryAuth) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(repositoryAuth) as T
    }
}