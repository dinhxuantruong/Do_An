package com.example.datn.data.dataresult.orders

data class Order(
    val id: Int,
    val items: List<Item>,
    val orders_address_id: Int,
    val total: Double,
    val user_id: Int,
    val review_status : Int,
)