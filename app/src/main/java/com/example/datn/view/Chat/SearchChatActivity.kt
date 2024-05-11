package com.example.datn.view.Chat

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.datn.adapter.messageAdapter
import com.example.datn.data.model.Users
import com.example.datn.databinding.ActivitySearchChatBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import java.util.Locale

class SearchChatActivity : AppCompatActivity() {
    private var _binding: ActivitySearchChatBinding? = null
    private val binding get() = _binding!!

    private var _adapter: messageAdapter? = null
    private val adapter get() = _adapter!!

    private lateinit var list: ArrayList<Users>

    private var firebaseUserId: String? = null
    private var db: DatabaseReference? = null
    private var dbListener: ValueEventListener? = null
    private var auth: FirebaseAuth? = null
    private var query: Query? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySearchChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        retrieveAllUser()

        binding.txtSearch.addTextChangedListener(searchWatcher)
    }


    private val searchWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // Không cần thực hiện gì trước khi thay đổi
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            searchForUsers(s.toString().lowercase(Locale.ROOT))

        }

        override fun afterTextChanged(s: Editable?) {

        }
    }

    private fun retrieveAllUser() {
        dbListener = db!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (binding.txtSearch.text.toString() == "") {
                    if (snapshot.exists()) {
                        list.clear()
                        for (s in snapshot.children) {
                            val userSnapshot = s.getValue(Users::class.java)
                            userSnapshot?.let { user ->
                                if (user.uid != firebaseUserId) {
                                    list.add(user)
                                }
                            }
                        }
                        _adapter = messageAdapter(this@SearchChatActivity,
                            object : messageAdapter.onClickMessage {
                                override fun onClick(itemUser: Users) {
                                    intentActivity(itemUser)
                                }
                            }, list, false
                        )
                        binding.searchRec.adapter = adapter
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Xử lý khi có lỗi xảy ra
            }
        })
    }


    private fun searchForUsers(str: String) {
        query = FirebaseDatabase.getInstance().reference
            .child("Users")
            .orderByChild("search")
            .startAt(str.toLowerCase(Locale.getDefault()))
            .endAt(str.toLowerCase(Locale.getDefault()) + "\uf8ff")

        dbListener = query!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                list.clear() // Xóa dữ liệu cũ
                for (snapshot in dataSnapshot.children) {
                    val userSnapshot = snapshot.getValue(Users::class.java)
                    userSnapshot?.let { user ->
                        if (user.uid != firebaseUserId) {
                            Log.e("UID", user.uid.toString())
                            Log.e("firebaseUserId", firebaseUserId.toString())
                            list.add(user)
                        }
                    }
                }
                _adapter = messageAdapter(this@SearchChatActivity,
                    object : messageAdapter.onClickMessage {
                        override fun onClick(itemUser: Users) {
                            intentActivity(itemUser)
                        }
                    }, list, false
                )
                binding.searchRec.adapter = adapter
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Xử lý khi có lỗi xảy ra
            }
        })
    }

    private fun intentActivity(itemUser : Users){
        val intent = Intent(this@SearchChatActivity,ChatActivity::class.java)
        val id = itemUser.uid
        intent.putExtra("id",id)
        startActivity(intent)
    }

    private fun init() {
        list = arrayListOf()
        binding.searchRec.setHasFixedSize(true)
        auth = FirebaseAuth.getInstance()
        firebaseUserId = auth!!.currentUser!!.uid
        db = FirebaseDatabase.getInstance().reference.child("Users")
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.txtSearch.removeTextChangedListener(searchWatcher)
        db?.removeEventListener(dbListener!!)
        query?.removeEventListener(dbListener!!)
        query = null
        db = null
        _adapter = null
        _binding = null
    }


}