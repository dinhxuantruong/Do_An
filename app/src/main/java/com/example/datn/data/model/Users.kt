package com.example.datn.data.model

import android.provider.ContactsContract.Profile

data class Users(
    val profile : String? = null,
    val uid: String? = null,
    val username: String? = null,
    val status: String? = null,
    val search: String? = null
)
