package com.example.datn.utils.Extention

import android.app.Activity
import android.view.View
import com.google.android.material.snackbar.Snackbar
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Locale

object NumberExtensions {
    fun Int.toVietnameseCurrency(): String {
        val formatter = NumberFormat.getNumberInstance(Locale("vi", "VN")) as DecimalFormat
        formatter.applyPattern("#,### đ")
        return formatter.format(this)
    }

    fun Long.toVietnameseCurrencyLong(): String {
        val formatter = NumberFormat.getNumberInstance(Locale("vi", "VN")) as DecimalFormat
        formatter.applyPattern("#,### đ")
        return formatter.format(this)
    }

    fun Float.toVietnameseCurrencyFloat(): String {
        val formatter = NumberFormat.getNumberInstance(Locale("vi", "VN")) as DecimalFormat
        formatter.applyPattern("#,###.# đ")
        return formatter.format(this)
    }

    fun Double.toVietnameseCurrencyDouble(): String {
        val formatter = NumberFormat.getNumberInstance(Locale("vi", "VN")) as DecimalFormat
        formatter.applyPattern("#,###.# đ")
        return formatter.format(this)
    }

    fun formatNumber(number: Int): String {
        val suffixes = arrayOf("", "k", "M", "B", "T")
        var value = number.toDouble()
        var index = 0
        while (value >= 1000 && index < suffixes.size - 1) {
            value /= 1000
            index++
        }
        // Format the number to either have no decimal places or one decimal place if needed
        val formattedValue = if (value == value.toInt().toDouble()) {
            value.toInt().toString()
        } else {
            String.format("%.1f", value)
        }
        return "$formattedValue${suffixes[index]}"
    }

    fun Activity.snackBar(message: String) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).also { snackbar ->
            snackbar.setAction("Ok") {
                snackbar.dismiss()
            }
        }.show()
    }


}