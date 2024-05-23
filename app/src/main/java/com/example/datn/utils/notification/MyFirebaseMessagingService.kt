package com.example.datn.utils.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import androidx.core.app.NotificationCompat
import com.example.datn.R
import com.example.datn.view.Chat.ChatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService() : FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val sented = message.data["sented"]
        val user = message.data["user"]

        val sharePref = getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        val currentOnlineUser = sharePref.getString("currentUser", "none")
        val firebaseUser = FirebaseAuth.getInstance().currentUser

        if (firebaseUser != null && sented == firebaseUser.uid) {
            if (currentOnlineUser != user) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    sendOreoNotification(message)
                } else {
                    sendNotification(message)
                }
            }
        }
    }

    private fun sendNotification(message: RemoteMessage) {
        val user = message.data["user"]
        val icon = message.data["icon"]?.toInt() ?: R.drawable.iconhome
        val title = message.data["title"]
        val body = message.data["body"]

        val notification = message.notification
        val j = user!!.replace(("[\\D]").toRegex(), "").toInt()
        val intent = Intent(this, ChatActivity::class.java)

        val bundle = Bundle()
        bundle.putString("userid", user)
        intent.putExtras(bundle)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)


        val pendingIntent = PendingIntent.getActivity(
            this, j, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )
        val defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val channelId = "datn-b3605"
        val builder: NotificationCompat.Builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(icon)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setSound(defaultSound)
            .setContentIntent(pendingIntent)

        val noti = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Tạo kênh thông báo nếu cần thiết
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "Channel human readable title"
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            noti.createNotificationChannel(channel)
        }

        var i = 0
        if (j > 0) {
            i = j
        }

        noti.notify(i, builder.build())
    }

    private fun sendOreoNotification(message: RemoteMessage) {
        val user = message.data["user"]
        val icon = message.data["icon"]
        val title = message.data["title"]
        val body = message.data["body"]

        val notification = message.notification
        val j = user!!.replace(("[\\D]").toRegex(), "").toInt()
        val intent = Intent(this, ChatActivity::class.java)

        val bundle = Bundle()
        bundle.putString("userid", user)
        intent.putExtras(bundle)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(
            this, j, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )
        val defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
         val oreoNotification = OreoNotification(this)

        val builder : Notification.Builder = oreoNotification.getOreoNotification(title,body,pendingIntent,defaultSound,icon)

        var i = 0
        if (j>0){
            i = j
        }

        oreoNotification.getManager!!.notify(i,builder.build())
    }
}