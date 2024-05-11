package com.example.datn.view.Chat

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.datn.data.model.Users
import com.example.datn.databinding.ActivityMessageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class MessageActivity : AppCompatActivity() {
    private var _binding: ActivityMessageBinding? = null

    private val binding get() = _binding!!
    private  var auth: FirebaseAuth? = null
    private  var db: DatabaseReference? = null
    private var dbListener: ValueEventListener? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        val id = auth!!.currentUser!!.uid
        db = FirebaseDatabase.getInstance().getReference("Users").child(id.toString())


        setView()

        binding.btn.setOnClickListener {
            startActivity(Intent(this@MessageActivity,SearchChatActivity::class.java))
        }

    }

    private fun setView() {
        dbListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.getValue(Users::class.java)!!
                    Picasso.get().load(user.profile).into(binding.image)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Xử lý lỗi khi có
            }
        }

        db!!.addValueEventListener(dbListener!!)
    }


    override fun onDestroy() {
        super.onDestroy()
        db?.removeEventListener(dbListener!!)
        auth = null
        db = null
        _binding = null
    }
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}