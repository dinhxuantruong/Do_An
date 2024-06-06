package com.example.datn.data.dataresult

data class resultStatistic(
    val monthly_revenue: List<MonthlyRevenue>,
    val revenue_change_percentage: String,
    val total_product_types: Int,
    val total_revenue_year: Int,
    val total_users: Int
)
data class MonthlyRevenue(
    val month: Int,
    val total_revenue: Double
)