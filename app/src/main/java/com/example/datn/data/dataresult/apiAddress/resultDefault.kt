package com.example.datn.data.dataresult.apiAddress

data class resultDefault(
    val default_address: DefaultAddress
)

data class DefaultAddress(
    val address: String,
    val country: Any,
    val district: String,
    val id: Int,
    val is_default: Int,
    val phone: String,
    val postal_code: Any,
    val province: String,
    val username: String,
    val ward: String
)