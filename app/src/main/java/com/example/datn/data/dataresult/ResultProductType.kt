package com.example.datn.data.dataresult

data class ProductType(
    val ProductTypes: List<ProductTypeX>
)

data class ProductTypeX(
    val id: Int,
    val image_url: String,
    val productlikes_count : Int,
    val name: String,
    val price: Int,
    val quantity : String,
    val sold_quantity : Int,
    val id_category : String
)