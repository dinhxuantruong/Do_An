package com.example.datn.view.Chat


import android.content.Intent

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.datn.databinding.ActivityAdviseBinding
import com.example.datn.utils.SharePreference.PrefManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.Locale

class AdviseActivity : AppCompatActivity() {
    private var _binding: ActivityAdviseBinding? = null
    private val binding get() = _binding!!
    private var _prefManager: PrefManager? = null
    private val prefManager get() = _prefManager!!
    private var _currentUser: FirebaseUser? = null
    private val currentUser get() = _currentUser!!
    private var _auth: FirebaseAuth? = null
    private val auth get() = _auth!!
    private lateinit var refUsers: DatabaseReference
    private lateinit var id: String
    private val REQUEST_CALL_PERMISSION = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAdviseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()

        // Khởi tạo auth ở đây trước khi sử dụng
        _auth = FirebaseAuth.getInstance()
//        _currentUser = auth.currentUser // Lấy currentUser sau khi đã khởi tạo auth
//        if (currentUser != null) {
//            startActivity(Intent(this@AdviseActivity, MessageActivity::class.java))
//            finish()
//        }


        binding.btnLogin.setOnClickListener {
            login()
        }

        binding.btnLogout.setOnClickListener {
            logout()
        }

        binding.btnCall.setOnClickListener {
            val numberPhone = "0373497770"
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Uri.encode(numberPhone)))
            startActivity(intent)
        }
    }



    private fun logout() {
        FirebaseAuth.getInstance().signOut()
    }


    private fun login() {
        val email = prefManager.getEmail()!!
        val password = prefManager.getEmail()!!
        auth.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    startActivity(Intent(this@AdviseActivity, MessageActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(
                        this@AdviseActivity,
                        "Incorrect account or password",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }


    private fun init() {
        _prefManager = PrefManager(this@AdviseActivity)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Giải phóng tài nguyên khi Activity bị hủy
        _auth = null
        _currentUser = null
        _prefManager = null
        _binding = null
    }
}
