package com.example.datn.data.dataresult

data class ResultMessage(
    val message: String,
    val email: String,
    val status: Boolean,
    val productlikes_count: Int,
    val total: Int,
    val count : Int,
    val item_count : Int,
    val id_type : String,
    val order : order
)

data class order(
    val final_amount : Double
)
