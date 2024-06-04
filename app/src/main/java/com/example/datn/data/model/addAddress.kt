package com.example.datn.data.model

data class addAddress(
    val address : String,
    val province : String,
    val district : String,
    val ward : String,
    val postal_code : String?,
    val country : String?,
    val is_default : String,
    val username : String,
    val phone : String,
)
