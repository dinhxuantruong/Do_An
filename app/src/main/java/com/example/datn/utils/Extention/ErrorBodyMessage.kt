package com.example.datn.utils.Extention

import com.google.gson.Gson
import com.google.gson.JsonObject
import retrofit2.Response


object ErrorBodyMessage {
    fun <T> Response<T>.getErrorBodyMessage(): String {
        val errorBody = this.errorBody()?.string()
        val gson = Gson()
        val errorJson: JsonObject? = gson.fromJson(errorBody, JsonObject::class.java)
        return errorJson?.get("message")?.asString ?: "Unknown error"
    }
}