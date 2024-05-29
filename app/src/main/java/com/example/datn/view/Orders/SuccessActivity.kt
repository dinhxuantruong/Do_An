package com.example.datn.view.Orders

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.datn.R
import com.example.datn.databinding.ActivitySuccessBinding
import com.example.datn.view.MainActivity2
import com.example.datn.view.MainView.MainViewActivity

class SuccessActivity : AppCompatActivity() {
    private var _binding : ActivitySuccessBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySuccessBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.btnContinue.setOnClickListener {
          startActivity(Intent(this@SuccessActivity,MainActivity2::class.java))
        }

    }

    private fun finishView(){
        finish()
    }
    override fun onBackPressed() {
        super.onBackPressed()
        finishView()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}