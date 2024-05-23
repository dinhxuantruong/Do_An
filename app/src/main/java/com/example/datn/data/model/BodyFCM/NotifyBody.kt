package com.example.datn.data.model.BodyFCM

data class NotifyBody(
    val message: Message
)
data class Notification(
    val body: String,
    val color: String,
    val icon: String,
    val title: String,
    val image : String
)

data class Message(
    val android: Android,
    val token: String
)


data class Android(
    val notification: Notification,
    val priority: String
)

