package com.example.datn.view.Admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.example.datn.R
import com.example.datn.databinding.ActivityDescriptionBinding
import com.example.datn.repository.repositoryAdmin
import com.example.datn.utils.SharePreference.PrefManager
import com.example.datn.viewmodel.Admin.AdminViewModel
import com.example.datn.viewmodel.Admin.AdminViewModelFactory

class DescriptionActivity : AppCompatActivity() {
    private var _binding : ActivityDescriptionBinding? = null
    private val binding get() = _binding!!
    private var _prefManager: PrefManager? = null
    private val prefManager get() = _prefManager!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDescriptionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.edittext.setStylesBar(binding.stylesbar)

        _prefManager = PrefManager(this)
        val text = intent.getStringExtra("desc")
        if (text!!.isNotEmpty()){
            binding.edittext.setText(text)
        }

        binding.btnSubmit.setOnClickListener {
            prefManager.saveText(binding.edittext.text.toString())
            finish()
        }


    }


    override fun onDestroy() {
        super.onDestroy()
        _prefManager = null
        _binding = null
    }
}