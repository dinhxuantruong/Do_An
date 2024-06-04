package com.example.datn.data.dataresult

data class resultHistoryRating(
    val image_url: String,
    val name: String,
    val reviews: List<ReviewRate>
)

data class ReviewRate(
    val comment: String,
    val created_at: String,
    val id: Int,
    val id_type: Int,
    val image_url: String,
    val name: String,
    val rating: Double
)