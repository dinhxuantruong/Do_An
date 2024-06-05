package com.example.datn.view.Chat

import FirebaseListenerObserver
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleObserver
import com.example.datn.adapter.messageAdapter
import com.example.datn.data.model.Chat
import com.example.datn.data.model.Chatlist
import com.example.datn.data.model.Token
import com.example.datn.data.model.Users
import com.example.datn.databinding.ActivityMessageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.messaging.FirebaseMessaging
import com.squareup.picasso.Picasso

class MessageActivity : AppCompatActivity() {
    private var _binding: ActivityMessageBinding? = null
    private val binding get() = _binding!!
    private var auth: FirebaseAuth? = null
    private var db: DatabaseReference? = null
    private var dbListener: ValueEventListener? = null
    private var _adapter: messageAdapter? = null
    private val adapter get() = _adapter!!

    private lateinit var list: ArrayList<Users>
    private lateinit var userChatList: ArrayList<Chatlist>
    private lateinit var refListChat: DatabaseReference
    private lateinit var id: String
    private lateinit var reference: DatabaseReference
    private var countMessage = 0
    private val firebaseListeners = mutableListOf<LifecycleObserver>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        setView()

        binding.btn.setOnClickListener {
            startActivity(Intent(this@MessageActivity, SearchChatActivity::class.java))
        }
    }

    private fun retrieveChatList() {
        val ref = FirebaseDatabase.getInstance().reference.child("Users")
        val chatListListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                list.clear()
                for (data in snapshot.children) {
                    val user = data.getValue(Users::class.java)
                    val chatList = userChatList.find { it.id == user?.uid }
                    if (chatList != null) {
                        list.add(user!!)
                    }
                }
                list.sortByDescending { user ->
                    userChatList.find { it.id == user.uid }?.lastMessageTimestamp ?: 0
                }
                Log.e("MAIMM", userChatList.sortedByDescending { it.lastMessageTimestamp }.toString())
                Log.e("MAIMM", list.toString())
                updateMessageAdapter()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error if needed
            }
        }

        ref.addListenerForSingleValueEvent(chatListListener)
        firebaseListeners.add(FirebaseListenerObserver(ref, chatListListener).apply {
            lifecycle.addObserver(this)
        })
    }

    private fun updateMessageAdapter() {
        _adapter = messageAdapter(
            this@MessageActivity,
            object : messageAdapter.onClickMessage {
                override fun onClick(itemUser: Users) {
                    val intent = Intent(this@MessageActivity, ChatActivity::class.java)
                    intent.putExtra("id", itemUser.uid)
                    startActivity(intent)
                }
            }, list, true
        )
        binding.listMessage.adapter = adapter
    }

    private fun init() {
        list = arrayListOf()
        userChatList = arrayListOf()
        auth = FirebaseAuth.getInstance()
        id = auth?.currentUser?.uid ?: ""
        db = FirebaseDatabase.getInstance().getReference("Users").child(id)
        refListChat = FirebaseDatabase.getInstance().reference.child("ChatsList").child(id)
        reference = FirebaseDatabase.getInstance().reference.child("Chats")

        // Listener cho ChatsList
        val refListChatListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userChatList.clear()
                for (datasnap in snapshot.children) {
                    val chatList = datasnap.getValue(Chatlist::class.java)
                    if (chatList != null) {
                        userChatList.add(chatList)
                    }
                }
                retrieveChatList()
                FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val refreshToken = task.result.toString()
                        updateToken(refreshToken)
                    } else {
                        Log.e("FCM", "Lỗi khi lấy token", task.exception)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error if needed
            }
        }

        refListChat.addValueEventListener(refListChatListener)
        firebaseListeners.add(FirebaseListenerObserver(refListChat, refListChatListener).apply {
            lifecycle.addObserver(this)
        })

        // Listener cho Chats để đếm tin nhắn chưa đọc
        val referenceListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                countMessage = 0 // Reset countMessage to avoid duplication
                for (dataSnap in snapshot.children) {
                    val chat = dataSnap.getValue(Chat::class.java)
                    if (chat?.receiver == auth?.uid && chat?.isseen == false) {
                        countMessage += 1
                    }
                }
                binding.txtSent.text = "Tin nhắn chưa đọc($countMessage)"
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error if needed
            }
        }

        reference.addValueEventListener(referenceListener)
        firebaseListeners.add(FirebaseListenerObserver(reference, referenceListener).apply {
            lifecycle.addObserver(this)
        })

        // Listener cho trạng thái của người dùng
        val usersRef = FirebaseDatabase.getInstance().reference.child("Users")
        val userStatusListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Trạng thái của người dùng đã thay đổi, cập nhật lại danh sách chat
                retrieveChatList()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error if needed
            }
        }

        usersRef.addValueEventListener(userStatusListener)
        firebaseListeners.add(FirebaseListenerObserver(usersRef, userStatusListener).apply {
            lifecycle.addObserver(this)
        })
    }

    private fun updateToken(refreshToken: String?) {
        val ref = FirebaseDatabase.getInstance().reference.child("Tokens")
        val token1 = Token(refreshToken!!)
        ref.child(id).setValue(token1)
    }

    private fun setView() {
        dbListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.getValue(Users::class.java)
                    user?.let {
                        Picasso.get().load(it.profile).into(binding.image)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error if needed
            }
        }

        db?.addValueEventListener(dbListener!!)
        firebaseListeners.add(FirebaseListenerObserver(db!!, dbListener!!).apply {
            lifecycle.addObserver(this)
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        firebaseListeners.forEach { lifecycle.removeObserver(it) }
        db = null
        auth = null
        _adapter = null
        _binding = null
    }
}
