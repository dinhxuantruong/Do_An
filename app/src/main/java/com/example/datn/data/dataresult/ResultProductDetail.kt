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
    val count_rating : Int,
    val lowest_price: Int,
    val made: String,
    val status : Int,
    val name: String,
    val package_size: String,
    val productlikes_count: Int,
    val products: List<Product>,
    val quantity: String,
    val recipe: String,
    val sold_quantity: Int,
    val trademark: String,
    val updated_at: String,
    val weight: String,
    val average_rating: Double, // Thêm thuộc tính này
    val reviews: List<Review>   // Thêm thuộc tính này
)

data class Review(
    val id: Int,
    val user_id: Int,
    val product_type_id: Int,
    val order_id: Int,
    val rating: Double,
    val comment: String?,
    val created_at: String,
    val updated_at: String,
    val user: UserNew,
    val human_readable_createAt: String,
)

data class UserNew(
    val id: Int,
    val name: String,
    val email: String,
    val role: Int,
    val profession: String,
    val phone: String,
    val image_url: String
)