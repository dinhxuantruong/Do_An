package com.example.datn.data.dataresult

data class resultCart(
    val itemCartsWithTotal: List<ItemCartsWithTotal>
)

data class ItemCartsWithTotal(
    val cart_id: Int,
    val id: Int,
    val laravel_through_key: Int,
    val product: ProductXX,
    val product_id: Int,
    var quantity: Int,
    val status: Int,
    val total: Int,
    var checkBoxAll : Boolean
)

data class ProductXX(
    val human_readable_createAt: String,
    val id: Int,
    val id_type: Int,
    val image_url: String,
    val price: Int,
    val product_quantity: String,
    val product_type_name: String,
    val size: String,
    val stock: Int
)