package com.example.datn.data.dataresult

data class resultAllVoucher(
    val coupons: List<Coupon>,
    val total_coupons : Int
)

data class Coupon(
    val code: String,
    val created_at: String,
    val discount_type: String,
    val discount_value: Double,
    val end_date: String,
    val id: Int,
    val start_date: String,
    val updated_at: String,
    val usage_limit: Int,
    val used: Int,
    val is_valid : Boolean
)