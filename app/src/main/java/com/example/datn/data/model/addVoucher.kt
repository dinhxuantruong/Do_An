package com.example.datn.data.model

data class addVoucher(
    val code : String,
    val discount_type : String,
    val discount_value : Double,
    val end_date : String,
    val usage_limit : Int,
    val start_date : String
)
