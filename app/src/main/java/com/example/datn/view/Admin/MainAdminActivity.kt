package com.example.datn.view.Admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.datn.R
import com.example.datn.databinding.ActivityMainAdminBinding
import com.example.datn.repository.repositoryAdmin
import com.example.datn.utils.SharePreference.PrefManager
import com.example.datn.view.Chat.MessageActivity
import com.example.datn.viewmodel.Admin.AdminViewModel
import com.example.datn.viewmodel.Admin.AdminViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.Locale

class MainAdminActivity : AppCompatActivity() {
    private var _binding : ActivityMainAdminBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: AdminViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val repository = repositoryAdmin()
        val vmFactory = AdminViewModelFactory(repository)
        viewModel = ViewModelProvider(this,vmFactory)[AdminViewModel::class.java]

        replaceFragment(ListProductFragment())
        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.listAdd -> replaceFragment(ListProductFragment())
                R.id.listOder -> replaceFragment(ListOrderFragment())
                R.id.listMore-> replaceFragment(ListSettingFragment())
                else -> {
                }
            }
            true
        }

    }


    private fun replaceFragment(fragment : Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout,fragment)
        fragmentTransaction.commit()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}