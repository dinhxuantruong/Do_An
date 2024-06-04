package com.example.datn.data.dataresult.apiAddress

data class resultListAddress(
    val addresses: List<Addresse>
)

data class Addresse(
    val address: String,
    val country: Any,
    val created_at: String,
    val district: String,
    val id: Int,
    var is_default: Int,
    val phone: String,
    val postal_code: Any,
    val province: String,
    val updated_at: String,
    val user_id: Int,
    val username: String,
    val ward: String
)