package com.example.datn.data.dataresult

data class resultCategory(
    val categories: List<Category>
)

data class Category(
    val created_at: String,
    val id: Int,
    val is_active: Int,
    val name: String,
    val updated_at: String
)