package com.example.datn.viewmodel.Auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.datn.repository.repositoryAuth

class AuthViewModelFactory(private val repositoryAuth: repositoryAuth) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AuthViewModel(repositoryAuth) as T
    }
}