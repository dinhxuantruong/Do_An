package com.example.datn.data.roomDatabase

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.datn.data.model.HistoryItemSearch

@Database(entities = [HistoryItemSearch.SearchHistory::class], version = 1, exportSchema = false)
abstract class DatabaseSearch : RoomDatabase() {

    abstract fun searchDao() : SearchDao
}