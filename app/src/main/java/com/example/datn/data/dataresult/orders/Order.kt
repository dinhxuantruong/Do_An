package com.example.datn.data.dataresult.orders

data class Order(
    val id: Int,
    val status : Int,
    val items: List<Item>,
    val orders_address_id: Int,
    val total: Double,
    val user_id: Int,
    val check : Boolean,
    val discount : Double,
    val final_amount : Double,
    val review_status : Int,
)