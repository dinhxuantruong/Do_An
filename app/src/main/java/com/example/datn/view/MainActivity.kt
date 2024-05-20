package com.example.datn.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.datn.databinding.ActivityMainBinding

import com.example.datn.view.Auth.AuthActivity
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp


class MainActivity : AppCompatActivity() {
    private var _binding : ActivityMainBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        binding.imageView.alpha = 0f
//        binding.imageView.animate().setDuration(1500).alpha(1f).withEndAction {
            startActivity(Intent(this, AuthActivity::class.java))
         //   overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
            finish()
    //    }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}