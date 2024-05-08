package com.example.datn.data.dataresult

sealed class ResponseResult<out T>{
    data class Success<T>(val data: T) : ResponseResult<T>()
    data class Error(val message: String) : ResponseResult<Nothing>()

}
