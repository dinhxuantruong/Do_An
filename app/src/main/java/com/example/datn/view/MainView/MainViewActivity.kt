package com.example.datn.view.MainView

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.view.get
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.datn.R
import com.example.datn.databinding.ActivityMainViewBinding
import com.example.datn.repository.repositoryAuth
import com.example.datn.utils.DataResult
import com.example.datn.utils.SharePreference.PrefManager
import com.example.datn.view.Auth.AuthActivity
import com.example.datn.view.Detail.ProductActivity
import com.example.datn.viewmodel.Auth.AuthViewModel
import com.example.datn.viewmodel.Auth.AuthViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainViewActivity : AppCompatActivity() {
    private var _binding: ActivityMainViewBinding? = null
    private val binding get() = _binding!!
    private var _prefManager: PrefManager? = null
    private val prefManager get() = _prefManager!!
    private lateinit var viewModel: AuthViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()

        binding.fab.setOnClickListener {
            startActivity(Intent(this,ProductActivity::class.java))
//            finish()
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
    val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
    val navController = findNavController(R.id.fragment)
    bottomNavigationView.setupWithNavController(navController)
    binding.bottomNavigationView.background = null
    binding.bottomNavigationView.menu[2].isEnabled = false
    _prefManager = PrefManager(this)
    val repositoryAuth = repositoryAuth(this)
    val vmFactory = AuthViewModelFactory(repositoryAuth)
    viewModel = ViewModelProvider(this,vmFactory)[AuthViewModel::class.java]
}

override fun onDestroy() {
    super.onDestroy()
    _binding = null
}
}