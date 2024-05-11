package com.example.datn.view.Chat

import android.content.Intent
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
    private var _currentUser: FirebaseUser? = null // Đặt giá trị mặc định là null
    private val currentUser get() = _currentUser!!
    private var _auth: FirebaseAuth? = null // Đặt giá trị mặc định là null
    private val auth get() = _auth!!
    private lateinit var refUsers: DatabaseReference
    private lateinit var id: String
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

        binding.btnRegister.setOnClickListener {
            register()
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


    private fun register() {
        val email = prefManager.getEmail()!!
        val password = prefManager.getEmail()!!
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { it ->
            if (it.isSuccessful) {
                prefManager.setLoginFireBase(true)
                Toast.makeText(this@AdviseActivity, "Register is successfully", Toast.LENGTH_SHORT)
                    .show()
                id = auth.currentUser!!.uid
                refUsers =
                    FirebaseDatabase.getInstance().reference.child("Users").child(id.toString())
                val userHashMap = HashMap<String, Any>()
                userHashMap["uid"] = id
                userHashMap["username"] = email
                userHashMap["profile"] = "https://picsum.photos/200 "
                userHashMap["status"] = "offline"
                userHashMap["search"] = email.lowercase(Locale.ROOT)
                refUsers.updateChildren(userHashMap).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Register update is successful", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        Toast.makeText(
                            this,
                            "Failed to update user: ${task.exception?.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } else {
                Toast.makeText(this, "Register failed: ${it.exception?.message}", Toast.LENGTH_LONG)
                    .show()
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
