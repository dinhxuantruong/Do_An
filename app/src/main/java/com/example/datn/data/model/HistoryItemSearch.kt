package com.example.datn.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

class HistoryItemSearch {
    @Parcelize
    @Entity(tableName = "search_table")
    data class SearchHistory(
        @PrimaryKey(autoGenerate = true)
        val id : Int,
        val searchtext : String,
    ) : Parcelable

}