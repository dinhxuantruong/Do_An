package com.example.datn.data.dataresult.orders

data class Item(
    val id: Int,
    val order_id: Int,
    val price: String,
    val product: Product,
    val product_id: Int,
    val quantity: Int
)