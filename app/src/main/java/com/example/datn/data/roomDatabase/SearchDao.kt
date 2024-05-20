package com.example.datn.data.roomDatabase

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.datn.data.model.HistoryItemSearch
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertData(person: HistoryItemSearch.SearchHistory)

    @Delete
    suspend fun deleteData(person: HistoryItemSearch.SearchHistory)

    @Update
    suspend fun updateData(person: HistoryItemSearch.SearchHistory)

    @Query("select * from search_table order by id asc")
    fun readAllData() : Flow<List<HistoryItemSearch.SearchHistory>>


    @Query("delete from search_table")
    suspend fun deleteAllData()

    @Query("select * from search_table where searchtext like :searchString")
    fun searchDatabase(searchString : String) : Flow<List<HistoryItemSearch.SearchHistory>>
}