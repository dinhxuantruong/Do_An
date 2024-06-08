package com.example.datn.data.dataresult

data class resultOrderDetails(
    val address: String,
    val uuid : String,
    val bank_transaction_id: Any,
    val created_at: String,
    val district: String,
    val items: List<Item>,
    val name: String,
    val order_id: Int,
    val payment_method: String,
    val phone: String,
    val province: String,
    val total: Double,
    val updated_at: String,
    val ward: String,
    val final_amount: Double,
    val discount: Double,
)
data class Type(
    val id: Int,
    val image_url: String,
    val name: String
)

data class Item(
    val id: Int,
    val order_id: Int,
    val price: String,
    val product: ProductX,
    val product_id: Int,
    val quantity: Int
)

data class ProductX(
    val human_readable_createAt: String,
    val id: Int,
    val id_type: Int,
    val image_url: String,
    val price: Int,
    val size: String,
    val stock: Int,
    val type: Type
)