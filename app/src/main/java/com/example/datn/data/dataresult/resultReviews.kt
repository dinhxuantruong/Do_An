package com.example.datn.data.dataresult

data class resultReviews(
    val meta: Meta,
    val reviews: List<Review>
)

data class Meta(
    val current_page: Int,
    val last_page: Int,
    val per_page: Int,
    val total: Int
)