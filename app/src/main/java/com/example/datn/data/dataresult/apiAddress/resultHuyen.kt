package com.example.datn.data.dataresult.apiAddress

data class resultHuyen(
    val `data`: List<DataX>,
    val data_name: String,
    val error: Int,
    val error_text: String
)

data class DataX(
    val full_name: String,
    val full_name_en: String,
    val id: String,
    val latitude: String,
    val longitude: String,
    val name: String,
    val name_en: String
)