package com.example.datn.data.dataresult

data class Result_slideimages(
    val data_result: List<DataResult>
)

data class DataResult(
    val id: Int,
    val image_url: String,
    val name: String
)