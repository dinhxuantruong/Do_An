package com.example.datn.view.Chat

import AccessToken
import FCMAPIService
import FirebaseListenerObserver
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleObserver
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.datn.adapter.ChatAdapter
import com.example.datn.data.dataresult.MyResponse
import com.example.datn.data.model.BodyFCM.Android
import com.example.datn.data.model.BodyFCM.Message
import com.example.datn.data.model.BodyFCM.Notification
import com.example.datn.data.model.BodyFCM.NotifyBody
import com.example.datn.data.model.Chat
import com.example.datn.data.model.Token
import com.example.datn.data.model.Users
import com.example.datn.databinding.ActivityChatBinding
import com.example.datn.utils.notification.Client
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import com.example.datn.R
import com.example.datn.data.model.Chatlist
import com.google.firebase.messaging.FirebaseMessaging
import retrofit2.Response

class ChatActivity : AppCompatActivity() {
    private var _binding: ActivityChatBinding? = null
    private val binding get() = _binding!!
    private var idUser = ""
    private var auth: FirebaseAuth? = null
    private var firebaseUser: FirebaseUser? = null
    private var _chatAdapter: ChatAdapter? = null
    private val adapter get() = _chatAdapter!!
    private lateinit var chatList: MutableList<Chat>
    private var reference: DatabaseReference? = null
    private var referenceChat: DatabaseReference? = null
    private val firebaseListeners = mutableListOf<LifecycleObserver>()

    private var seenListener: ValueEventListener? = null
    private var notify = false
    private var apiService: FCMAPIService? = null
    private var tokenAccess = ""
    private var id = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        setupClickListeners()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.chat_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.btnPhone -> {
                val numberPhone = "0373497770"
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Uri.encode(numberPhone)))
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupClickListeners() {
        binding.toolbarChatLDetails.setNavigationOnClickListener {
            finish()
        }
        binding.btnSend.setOnClickListener {
            notify = true
            val message = binding.txtMessage.text.toString().trim()
            if (message.isNotEmpty()) {
                sendMessageToUser(firebaseUser!!.uid, idUser, message)
                binding.txtMessage.text.clear()
            }
        }
        binding.idSendImage.setOnClickListener {
            notify = true
            val intent = Intent().apply {
                action = Intent.ACTION_GET_CONTENT
                type = "image/*"
            }
            startActivityForResult(Intent.createChooser(intent, "Pick Image"), 438)
        }

        apiService = Client.Client.getClient("https://fcm.googleapis.com/v1/")!!
            .create(FCMAPIService::class.java)
    }

    private fun init() {
        setSupportActionBar(binding.toolbarChatLDetails)
        CoroutineScope(Dispatchers.IO).launch {
            val accessToken = AccessToken().getAccessToken()
            Log.e("MAINN2", accessToken.toString())
            tokenAccess = "Bearer $accessToken"
        }
        auth = FirebaseAuth.getInstance()
        id = auth?.currentUser?.uid ?: ""
        val refListChat = FirebaseDatabase.getInstance().reference.child("ChatsList").child(id)
        val refListChatListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
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
        idUser = intent.getStringExtra("id").toString()
        chatList = mutableListOf()
        firebaseUser = FirebaseAuth.getInstance().currentUser
        binding.recyclerviewChat.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(applicationContext)
        linearLayoutManager.stackFromEnd = true
        binding.recyclerviewChat.layoutManager = linearLayoutManager

        reference = FirebaseDatabase.getInstance().reference.child("Users").child(idUser)

        val referenceListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(Users::class.java)!!
                binding.txtName.text = user.username
                Picasso.get().load(user.profile).into(binding.image)
                setViewMessage(firebaseUser!!.uid, idUser, user.profile)//leak
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle possible errors
            }
        }
        reference!!.addValueEventListener(referenceListener)
        val observer = FirebaseListenerObserver(reference!!, referenceListener)
        firebaseListeners.add(observer)
        lifecycle.addObserver(observer)

        seenMessage(idUser)
        updateStatus("online")
    }
    private fun updateToken(refreshToken: String?) {
        val ref = FirebaseDatabase.getInstance().reference.child("Tokens")
        val token1 = Token(refreshToken!!)
        ref.child(id).setValue(token1)
    }
    private fun setViewMessage(senderId: String, receiverID: String, imageUrl: String?) {
        val receiver = FirebaseDatabase.getInstance().reference.child("Chats")

        val referenceListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                chatList.clear()
                for (i in snapshot.children) {
                    val chat = i.getValue(Chat::class.java)!!
                    if ((chat.receiver == senderId && chat.sender == receiverID) ||
                        (chat.receiver == receiverID && chat.sender == senderId)
                    ) {
                        chatList.add(chat)
                    }
                }
                _chatAdapter = ChatAdapter(this@ChatActivity, chatList, imageUrl!!)
                binding.recyclerviewChat.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle possible errors
            }
        }
        receiver.addValueEventListener(referenceListener)
        val observer = FirebaseListenerObserver(receiver, referenceListener)
        firebaseListeners.add(observer)
        lifecycle.addObserver(observer)
    }

    private fun sendMessageToUser(sendID: String, receiverId: String, message: String) {
        val reference = FirebaseDatabase.getInstance().reference
        val chatsListenReference = FirebaseDatabase.getInstance().reference
            .child("ChatsList")
            .child(firebaseUser!!.uid)
            .child(idUser)
        val messageKey = reference.push().key
        val currentTimestamp = System.currentTimeMillis()

        val messageHasMap = HashMap<String, Any?>()
        messageHasMap["sender"] = sendID
        messageHasMap["message"] = message
        messageHasMap["receiver"] = receiverId
        messageHasMap["isseen"] = false
        messageHasMap["url"] = ""
        messageHasMap["messageID"] = messageKey
        messageHasMap["timestamp"] = currentTimestamp

        reference.child("Chats").child(messageKey!!).setValue(messageHasMap)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val referenceListener = object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (!snapshot.exists()) {
                                chatsListenReference.child("id").setValue(idUser)
                            }
                            val chatsListenReceiver = FirebaseDatabase.getInstance().reference
                                .child("ChatsList")
                                .child(idUser)
                                .child(firebaseUser!!.uid)

                            chatsListenReceiver.child("id").setValue(firebaseUser!!.uid)
                        }

                        override fun onCancelled(error: DatabaseError) {
                            // Handle possible errors
                        }
                    }
                    chatsListenReference.addValueEventListener(referenceListener)
                    val observer = FirebaseListenerObserver(chatsListenReference, referenceListener)
                    firebaseListeners.add(observer)
                    lifecycle.addObserver(observer)

                    // Cập nhật timestamp và lastMessageTimestamp trong ChatsList cho người gửi
                    val chatsListSenderReference = FirebaseDatabase.getInstance().reference
                        .child("ChatsList")
                        .child(sendID)
                        .child(receiverId)

                    val timestampMapForSender = HashMap<String, Any?>()
                    timestampMapForSender["timestamp"] = currentTimestamp
                    timestampMapForSender["lastMessageTimestamp"] = currentTimestamp

                    chatsListSenderReference.updateChildren(timestampMapForSender)

                    // Cập nhật timestamp và lastMessageTimestamp trong ChatsList cho người nhận
                    val chatsListReceiverReference = FirebaseDatabase.getInstance().reference
                        .child("ChatsList")
                        .child(receiverId)
                        .child(sendID)

                    val timestampMapForReceiver = HashMap<String, Any?>()
                    timestampMapForReceiver["timestamp"] = currentTimestamp
                    timestampMapForReceiver["lastMessageTimestamp"] = currentTimestamp

                    chatsListReceiverReference.updateChildren(timestampMapForReceiver)
                }
            }
        val referenceNew = FirebaseDatabase.getInstance().reference.child("Users")
            .child(firebaseUser!!.uid)
        val referenceListenerNew = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(Users::class.java)
                if (notify) {
                    sendNotification(receiverId, user!!.username, message,"")
                }
                notify = false
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle possible errors
            }
        }
        referenceNew.addValueEventListener(referenceListenerNew)
        val observer = FirebaseListenerObserver(referenceNew, referenceListenerNew)
        firebaseListeners.add(observer)
        lifecycle.addObserver(observer)
    }

    private fun sendNotification(receiverId: String, username: String?, message: String,url : String) {
        val ref = FirebaseDatabase.getInstance().reference.child("Tokens")
        val query = ref.orderByKey().equalTo(receiverId)

        val newQuery = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (data2 in snapshot.children) {
                    val token2 = data2.getValue(Token::class.java)
                    val token = token2!!.token
                    val android = Android(
                        Notification("$username: $message", "#1EA0DB", "siuu", "New message",url),
                        "high"
                    )
                    val message  = Message(android,token)
                    apiService!!.sendNotification(NotifyBody(message), tokenAccess)
                        .enqueue(object : Callback<MyResponse> {
                            override fun onResponse(
                                call: Call<MyResponse>,
                                response: Response<MyResponse>
                            ) {
                                if (response.code() == 200) {

                                } else {
                                    if (response.body()!!.success != 1) {
                                        Toast.makeText(
                                            this@ChatActivity,
                                            "Failed",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }

                            override fun onFailure(call: Call<MyResponse>, t: Throwable) {

                            }
                        })
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle possible errors
            }
        }
        query.addValueEventListener(newQuery)
        val observer = FirebaseListenerObserver(query, newQuery)
        firebaseListeners.add(observer)
        lifecycle.addObserver(observer)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 438 && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val loadingBar = ProgressDialog(this@ChatActivity)
            loadingBar.setMessage("Please wait, image is sending...")
            loadingBar.show()

            val fileUri = data.data
            val storeReference = FirebaseStorage.getInstance().reference.child("Chat Images")
            val ref = FirebaseDatabase.getInstance().reference
            val messageId = ref.push().key
            val filePath = storeReference.child("$messageId.jpg")

            val uploadTask = filePath.putFile(fileUri!!)
            uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation filePath.downloadUrl
            }).addOnCompleteListener { task ->
                loadingBar.dismiss() // Ensure loading bar is dismissed regardless of the outcome
                if (task.isSuccessful) {
                    val downloadUrl = task.result
                    val url = downloadUrl.toString()

                    val messageHasMap = HashMap<String, Any?>()
                    messageHasMap["sender"] = firebaseUser!!.uid
                    messageHasMap["message"] = "send you an image."
                    messageHasMap["receiver"] = idUser
                    messageHasMap["isseen"] = false
                    messageHasMap["url"] = url
                    messageHasMap["messageID"] = messageId

                    ref.child("Chats").child(messageId!!).setValue(messageHasMap)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val referenceNew =
                                    FirebaseDatabase.getInstance().reference.child("Users")
                                        .child(firebaseUser!!.uid)
                                val referenceListenerNew = object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        val user = snapshot.getValue(Users::class.java)
                                        if (notify) {
                                            sendNotification(
                                                idUser,
                                                user!!.username,
                                                "send you an image."
                                            ,url)
                                        }
                                        notify = false
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        // Handle possible errors
                                    }
                                }
                                referenceNew.addValueEventListener(referenceListenerNew)
                                val observer = FirebaseListenerObserver(referenceNew, referenceListenerNew)
                                firebaseListeners.add(observer)
                                lifecycle.addObserver(observer)
                            }
                        }
                } else {
                    Log.d("IMAGE","Failed to upload image: ${task.exception?.message}")
                }
            }
        }
    }

    private fun seenMessage(userID: String) {
        referenceChat = FirebaseDatabase.getInstance().getReference("Chats")
        seenListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnap in snapshot.children) {
                    val chat = dataSnap.getValue(Chat::class.java)
                    if (chat!!.receiver == firebaseUser!!.uid && chat.sender == userID) {
                        val hashMap = HashMap<String, Any>()
                        hashMap["isseen"] = true
                        dataSnap.ref.updateChildren(hashMap)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle possible errors
            }
        }
        referenceChat?.addValueEventListener(seenListener!!)
        val observer = FirebaseListenerObserver(referenceChat!!, seenListener!!)
        firebaseListeners.add(observer)
        lifecycle.addObserver(observer)
    }



    private fun updateStatus(status : String){
        val ref = FirebaseDatabase.getInstance().reference.child("Users").child(id)
        val hashMap = HashMap<String,Any>()
        hashMap["status"]   = status
        ref.updateChildren(hashMap)
    }


    override fun onResume() {
        super.onResume()
        seenMessage(idUser)
    }

    override fun onPause() {
        super.onPause()
        referenceChat?.removeEventListener(seenListener!!)
    }

    override fun onDestroy() {
        super.onDestroy()
        firebaseListeners.forEach { lifecycle.removeObserver(it) }
        firebaseListeners.clear()
        reference = null
        referenceChat = null
        _chatAdapter = null
        _binding = null
    }
}

