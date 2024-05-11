package com.example.datn.view.Chat

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.datn.adapter.ChatAdapter
import com.example.datn.data.model.Chat
import com.example.datn.data.model.Users
import com.example.datn.databinding.ActivityChatBinding
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso

class ChatActivity : AppCompatActivity() {
    private var _binding: ActivityChatBinding? = null
    private val binding get() = _binding!!
    private var idUser = ""

    private var firebaseUser: FirebaseUser? = null
    private var _chatAdapter: ChatAdapter? = null
    private val adapter get() = _chatAdapter!!
    private lateinit var chatList: MutableList<Chat>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()

        setupClickListeners()

    }
    private fun setupClickListeners() {
        binding.btnSend.setOnClickListener {
            val message = binding.txtMessage.text.toString().trim()
            if (message.isNotEmpty()) {
                sendMessageToUser(firebaseUser!!.uid, idUser, message)
                binding.txtMessage.text.clear()
            } else {
                Toast.makeText(this, "Chưa nhập dữ liệu", Toast.LENGTH_SHORT).show()
            }
        }

        binding.idSendImage.setOnClickListener {
            val intent = Intent().apply {
                action = Intent.ACTION_GET_CONTENT
                type = "image/*"
            }
            startActivityForResult(Intent.createChooser(intent, "Pick Image"), 438)
        }
    }

    private fun init() {
        idUser = intent.getStringExtra("id").toString()
        chatList = mutableListOf()
        firebaseUser = FirebaseAuth.getInstance().currentUser
        binding.recyclerviewChat.setHasFixedSize(true)
        var linerLayoutManager = LinearLayoutManager(applicationContext)
        linerLayoutManager.stackFromEnd = true
        binding.recyclerviewChat.layoutManager = linerLayoutManager

        val reference = FirebaseDatabase.getInstance().reference.child("Users").child(idUser)

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(Users::class.java)!!
                binding.txtName.text = user.username
                Picasso.get().load(user.profile).into(binding.image)
                setViewMessage(firebaseUser!!.uid,idUser,user.profile)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun setViewMessage(senderId: String, receiverID: String, imageUrl: String?) {
        val receiver = FirebaseDatabase.getInstance().reference.child("Chats")

        receiver.addValueEventListener(object  : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                chatList.clear()
                for (i in snapshot.children){
                    val chat = i.getValue(Chat::class.java)!!
                    if (chat!!.receiver.equals(senderId) && chat.sender.equals(receiverID)
                        || chat.receiver.equals(receiverID) && chat.sender.equals(senderId))
                    {
                        chatList.add(chat)
                    }
                }
                _chatAdapter = ChatAdapter(this@ChatActivity,chatList,imageUrl!!)
                binding.recyclerviewChat.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                //
            }
        })
    }

    private fun sendMessageToUser(sendID: String, receiverId: String, message: String) {
        val reference = FirebaseDatabase.getInstance().reference
        val messageKey = reference.push().key

        val messageHasMap = HashMap<String, Any?>()
        messageHasMap["sender"] = sendID
        messageHasMap["message"] = message
        messageHasMap["receiver"] = receiverId
        messageHasMap["isseen"] = false
        messageHasMap["url"] = ""
        messageHasMap["messageID"] = messageKey

        reference.child("Chats").child(messageKey!!).setValue(messageHasMap)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val chatsListenReference = FirebaseDatabase.getInstance().reference
                        .child("ChatsList")
                        .child(firebaseUser!!.uid)
                        .child(idUser)

                    chatsListenReference.addValueEventListener(object : ValueEventListener {
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
                            //
                        }
                    })
//                    chatsListenReference.child("id").setValue(firebaseUser!!.uid)
//
//                    chatsListenReference.child("id").setValue(firebaseUser!!.uid)


                }

            }


    }

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

            var uploadTask = filePath.putFile(fileUri!!)
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
                } else {
                    Toast.makeText(this@ChatActivity, "Failed to upload image: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _chatAdapter = null
        _binding = null
    }
}