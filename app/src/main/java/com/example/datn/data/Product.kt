package com.example.datn.data

data class Product(
    val human_readable_createAt: String,
    val id: Int,
    val id_type: Int,
    val image_url: String,
    val price: Int,
    val size: String,
    val stock: Int
)