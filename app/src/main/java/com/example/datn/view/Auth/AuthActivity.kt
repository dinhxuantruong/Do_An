package com.example.datn.view.Auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.datn.R
import com.example.datn.databinding.ActivityAuthBinding
import com.example.datn.repository.repositoryAuth
import com.example.datn.viewmodel.AuthViewModel
import com.example.datn.viewmodel.AuthViewModelFactory

class AuthActivity : AppCompatActivity() {
    private lateinit var viewModel: AuthViewModel
    private var _binding : ActivityAuthBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repository = repositoryAuth(this)
        val vmFactory = AuthViewModelFactory(repository)
        viewModel = ViewModelProvider(this,vmFactory)[AuthViewModel::class.java]

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}