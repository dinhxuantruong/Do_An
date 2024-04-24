package com.example.datn.view.MainView

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.example.datn.databinding.ActivityMainViewBinding
import com.example.datn.repository.repositoryAuth
import com.example.datn.utils.DataResult
import com.example.datn.utils.SharePreference.PrefManager
import com.example.datn.utils.SharePreference.UserPreferences
import com.example.datn.utils.Token.ACCESS_TOKEN
import com.example.datn.utils.network.RetrofitInstance
import com.example.datn.view.Auth.AuthActivity
import com.example.datn.viewmodel.MainViewModel
import com.example.datn.viewmodel.MainViewModelFactory


class MainViewActivity : AppCompatActivity() {
    private var _binding : ActivityMainViewBinding? = null
    private val binding get() = _binding!!
    private lateinit var  viewModel : MainViewModel
    private var _prefManager: PrefManager? = null
    private val prefManager get() = _prefManager!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()

        binding.button.setOnClickListener {
            viewModel.authLogout()
        }

        viewModel.resultLogout.observe(this){
            when(it){
                is DataResult.Success ->{
                    prefManager.removeDate()
                    startActivity(Intent(this,AuthActivity::class.java))
                }
                is DataResult.Error -> {
                    Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private fun init() {
        _prefManager = PrefManager(this)
        val userPreferences = UserPreferences(this)
        userPreferences.authToken.asLiveData().observe(this){
            RetrofitInstance.Token = it.toString()
        }
        val repositoryAuth = repositoryAuth(this)
        val vmFactory = MainViewModelFactory(repositoryAuth)
        viewModel = ViewModelProvider(this,vmFactory)[MainViewModel::class.java]
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}