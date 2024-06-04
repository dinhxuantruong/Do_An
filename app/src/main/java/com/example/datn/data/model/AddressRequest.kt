package com.example.datn.data.model

data class AddressRequest(
    val idaddress: Int,
    val payment_method_id : Int,
    val bank_transaction_id : Int?,
    val uuid : String
)
