package com.example.datn.data.model

data class loginWithGoogle(
    val id_token : String?,
    val refresh_token : String?,
    val client_id : String?,
    val client_secret : String? ,

    val grant_type : String,
)
