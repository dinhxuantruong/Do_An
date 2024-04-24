package com.example.datn.data

data class User(
    val message: String,
    val user: UserX
)

data class UserX(
    val access_token: String,
    val email: String,
    val id: Int,
    val name: String,
    val phone: Any,
    val profession: Any,
    val profile_photo: Any,
    val refresh_token: String,
    val role: Int,
    val status: Int
)