package com.example.datn.data.dataresult

data class resultProductTYpe(
    val productType: ProductTypeNew
)
data class ProductTypeNew(
    val active_ingredients: String,
    val created_at: String,
    val date: String,
    val description: String,
    val id: Int,
    val id_category: Int,
    val image_url: String,
    val ingredient: String,
    val made: String,
    val name: String,
    val package_size: String,
    val quantity: String,
    val recipe: String,
    val sold_quantity: Int,
    val trademark: String,
    val updated_at: String,
    val weight: String
)