package com.example.datn.data.dataresult

data class ResultMessage(
    val message: String,
    val email: String,
    val status: Boolean,
    val productlikes_count: Int,
    val total: Int,
    val count : Int,
    val item_count : Int,
)
