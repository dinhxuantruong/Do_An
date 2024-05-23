package com.example.datn.utils.notification

import android.util.Log
import com.example.datn.data.model.Token
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService

class MyFirebaseInstanceId : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val refreshToken = task.result
                if (firebaseUser != null) {
                    updateToken(refreshToken)
                }
            } else {
                // Xử lý lỗi nếu cần thiết
                Log.e("FCM", "Lỗi khi lấy token", task.exception)
            }
        }
    }

    private fun updateToken(refreshToken: String?) {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val ref = FirebaseDatabase.getInstance().reference.child("Tokens")
        val token = Token(refreshToken!!)
        ref.child(firebaseUser!!.uid).setValue(token)
    }

}