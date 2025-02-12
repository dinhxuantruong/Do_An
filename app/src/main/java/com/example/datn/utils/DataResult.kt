package com.example.datn.utils

sealed class DataResult<out T>{
    data class Success<T>(val data: T) : DataResult<T>()
    data class Error(val message: String) : DataResult<Nothing>()
}
