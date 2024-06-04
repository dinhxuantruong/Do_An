package com.example.datn.viewmodel.Admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.datn.repository.repositoryAdmin

class AdminViewModelFactory(private val repositoryAdmin: repositoryAdmin) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AdminViewModel(repositoryAdmin) as T
    }
}