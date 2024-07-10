package com.example.datn.view.MainView

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.datn.MyApplication
import com.example.datn.R
import com.example.datn.data.dataresult.ResponseResult
import com.example.datn.databinding.ActivityMainViewBinding
import com.example.datn.repository.repositoryAuth
import com.example.datn.utils.SharePreference.PrefManager
import com.example.datn.view.Auth.AuthActivity
import com.example.datn.view.Chat.AdviseActivity
import com.example.datn.view.Chat.ChatActivity
import com.example.datn.view.Chat.MessageActivity
import com.example.datn.viewmodel.Auth.AuthViewModel
import com.example.datn.viewmodel.Auth.AuthViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.Locale


class MainViewActivity : AppCompatActivity() {
    private var _binding: ActivityMainViewBinding? = null
    private val binding get() = _binding!!
    private var _prefManager: PrefManager? = null
    private val prefManager get() = _prefManager!!
    private var _auth: FirebaseAuth? = null
    private val auth get() = _auth!!
    private lateinit var id: String
    private lateinit var refUsers: DatabaseReference

    private lateinit var viewModel: AuthViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        val check = prefManager.isLoginFireBase()

        binding.fab.setOnClickListener {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                intentChat()
            }else {
                loginFirebase()
            }
        }
        if (check!!){
            register()
        }

        viewModel.resultLogout.observe(this){
            when(it){
                is ResponseResult.Success ->{
                    prefManager.removeDate()
                    startActivity(Intent(this, AuthActivity::class.java))
                }

                is ResponseResult.Error -> {
                    Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
                }

                else -> {}
            }
        }
        //updateStatus("online")
    }

//    override fun onResume() {
//        super.onResume()
//        updateStatus("online")
//    }
//
//    override fun onPause() {
//        super.onPause()
//        updateStatus("offline")
//    }
//
//
//      private fun updateStatus(status : String){
//        val ref = FirebaseDatabase.getInstance().reference.child("Users").child(auth.uid!!)
//        val hashMap = HashMap<String,Any>()
//        hashMap["status"]   = status
//        ref.updateChildren(hashMap)
//    }

    private fun loginFirebase() {
        val email = prefManager.getEmail()!!
        val password = prefManager.getEmail()!!
        auth.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    intentChat()
                } else {
                   Log.d("FIREBASE",task.exception?.message.toString())
                }
            }
    }

    private fun intentChat(){
        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra("id","MYtAsgE9pOS3rF4qBpWLoDlaR9x2")
        startActivity(intent)
    }
    private fun register() {
        val email = prefManager.getEmail()!!
        val password = prefManager.getEmail()!!
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { it ->
            if (it.isSuccessful) {
                prefManager.setLoginFireBase(false)
                Toast.makeText(
                    this@MainViewActivity,
                    "Register is successfully",
                    Toast.LENGTH_SHORT
                )
                    .show()
                id = auth.currentUser!!.uid
                refUsers =
                    FirebaseDatabase.getInstance().reference.child("Users").child(id.toString())
                val userHashMap = HashMap<String, Any>()
                userHashMap["uid"] = id
                userHashMap["username"] = email
                val url = prefManager.getUrl()
                if (url != null) {
                    userHashMap["profile"] = url
                } else {
                    userHashMap["profile"] = "https://picsum.photos/200 "
                }
                userHashMap["status"] = "offline"
                userHashMap["search"] = email.lowercase(Locale.ROOT)
                refUsers.updateChildren(userHashMap).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Register update is successful", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }
    }

    private fun init() {
        _auth = FirebaseAuth.getInstance()
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        val navController = findNavController(R.id.fragment)
        bottomNavigationView.setupWithNavController(navController)
        binding.bottomNavigationView.background = null
        binding.bottomNavigationView.menu[2].isEnabled = false
        _prefManager = PrefManager(this)
        val repositoryAuth = repositoryAuth(this)
        val vmFactory = AuthViewModelFactory(repositoryAuth)
        viewModel = ViewModelProvider(this, vmFactory)[AuthViewModel::class.java]
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}