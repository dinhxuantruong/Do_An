package com.example.datn.data.model

import java.sql.Timestamp

data class Chat(
    val sender: String? = null,
    val message: String? = null,
    val receiver: String? = null,
    val isseen: Boolean? = false,
    val url: String? = null,
    val messageID: String? = null,
    val timestamp: Long? = 0
)
