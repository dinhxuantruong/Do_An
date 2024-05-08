package com.example.datn.data.dataresult

data class Resutl_RefreshToken(
    val access_token: String,
    val expires_in: Int,
    val id_token: String,
    val refresh_token: String,
    val scope: String,
    val token_type: String
)