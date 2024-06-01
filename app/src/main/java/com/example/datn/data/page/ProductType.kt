package com.velmurugan.paging3android

data class ProductType(
    val id: Int,
    val image_url: String,
    val name: String,
    val price: Int,
    val quantity: String,
    val id_category : String,
    val sold_quantity : Int,
    val favorites_count : Int
)