package com.example.datn.data.dataresult

class resultBarrChart : ArrayList<resultBarrChartItem>()

data class resultBarrChartItem(
    val month: Int,
    val total_revenue: String,
    val year: Int
)