package com.example.datn.data.model

data class Chat(
    val sender: String? = null,
    val message: String? = null,
    val receiver: String? = null,
    val isseen: Boolean? = false,
    val url: String? = null,
    val messageID: String? = null
)