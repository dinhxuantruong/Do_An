package com.example.datn.utils.Extension

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.OpenableColumns
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
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
    fun setFabIconColor(context: Context,fab: FloatingActionButton, drawableResId: Int, colorResId: Int) {
        val drawable: Drawable? = ContextCompat.getDrawable(context, drawableResId)
        drawable?.setColorFilter(ContextCompat.getColor(context, colorResId), PorterDuff.Mode.SRC_IN)
        fab.setImageDrawable(drawable)
    }

    fun ContentResolver.getFileName(fileUri: Uri): String {
        var name = ""
        val returnCursor = this.query(fileUri, null, null, null, null)
        if (returnCursor != null) {
            val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            returnCursor.moveToFirst()
            name = returnCursor.getString(nameIndex)
            returnCursor.close()
        }
        return name
    }
}