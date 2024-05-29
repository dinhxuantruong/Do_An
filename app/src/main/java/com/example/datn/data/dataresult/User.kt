package com.example.datn.data.dataresult

data class User(
    val message: String,
    val user: UserX
)

data class UserX(
    val access_token: String,
    val email: String,
    val id: Int,
    val name: String,
    val phone: String,
    val profession: String,
    val profile_photo: String,
    val refresh_token: String,
    val role: Int,
    val status: Int
)