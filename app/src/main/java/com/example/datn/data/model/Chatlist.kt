package com.example.datn.data.model

import java.sql.Timestamp

data class Chatlist(
    val id: String? = null,
    val timestamp: Long? = 0,
    val lastMessageTimestamp: Long? = 0
)
