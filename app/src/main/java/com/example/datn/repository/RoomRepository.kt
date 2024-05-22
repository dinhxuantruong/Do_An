package com.example.datn.repository

import com.example.datn.data.model.HistoryItemSearch
import com.example.datn.data.roomDatabase.SearchDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RoomRepository @Inject constructor(private val personDao: SearchDao) {

    fun readAllData(): Flow<List<HistoryItemSearch.SearchHistory>> = personDao.readAllData()

    suspend fun insertData(person: HistoryItemSearch.SearchHistory) {
        personDao.insertData(person)
    }

    suspend fun updateData(person: HistoryItemSearch.SearchHistory) {
        personDao.updateData(person)
    }

    suspend fun deleteData(person: HistoryItemSearch.SearchHistory) {
        personDao.deleteData(person)
    }

    fun searchDatabase(text: String): Flow<List<HistoryItemSearch.SearchHistory>> {
        return personDao.searchDatabase(text)
    }

    suspend fun deleteAllData() {
        personDao.deleteAllData()
    }

    suspend fun getSearchHistoryByText(searchText: String): HistoryItemSearch.SearchHistory? {
        return personDao.getSearchHistoryByText(searchText)
    }
}
