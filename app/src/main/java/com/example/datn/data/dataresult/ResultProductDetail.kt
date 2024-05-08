package com.example.datn.data.dataresult

data class ResultProductDetail(
    val Images: List<String>,
    val ProductType: ProductTypeDetail,
    val message: String
)

data class ProductTypeDetail(
    val active_ingredients: String,
    val created_at: String,
    val date: String,
    val description: String,
    val human_readable_createAt: String,
    val id: Int,
    val id_category: Int,
    val image_url: String,
    val ingredient: String,
    val liked_by_current_user: Boolean,
    val lowest_price: Int,
    val made: String,
    val name: String,
    val package_size: String,
    val productlikes_count: Int,
    val products: List<Product>,
    val quantity: String,
    val recipe: String,
    val sold_quantity: Int,
    val trademark: String,
    val updated_at: String,
    val weight: String
)