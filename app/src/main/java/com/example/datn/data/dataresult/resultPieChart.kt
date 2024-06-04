package com.example.datn.data.dataresult

class resultPieChart : ArrayList<resultPieChartItem>()

data class resultPieChartItem(
    val category: String,
    val sold_percentage: Double
)