package com.example.datn.view.Admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.datn.R
import com.example.datn.databinding.ActivitySearchOrdersBinding

class SearchOrdersActivity : AppCompatActivity() {
    private var _binding : ActivitySearchOrdersBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySearchOrdersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSearch.setOnClickListener {
            val intent = Intent(this,ListSearchOrdersActivity::class.java)
            intent.putExtra("check",0)
            intent.putExtra("email",binding.txtEmail.text.toString())
            startActivity(intent)
        }

        binding.btnSearch.setOnLongClickListener{
            val intent = Intent(this,ListSearchOrdersActivity::class.java)
            intent.putExtra("check",1)
            intent.putExtra("uuid",binding.txtIdOrders.text.toString())
            startActivity(intent)
            true
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}