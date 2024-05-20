package com.example.datn.viewmodel.Products

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.datn.data.model.HistoryItemSearch
import com.example.datn.repository.RoomRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoomViewModel @Inject constructor(private val repository: RoomRepository) : ViewModel() {

    val readAllData  = repository.readAllData().asLiveData()

    fun insertData(person: HistoryItemSearch.SearchHistory){
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertData(person)
        }
    }

    fun updateData(person: HistoryItemSearch.SearchHistory){
        viewModelScope.launch {
            repository.updateData(person)
        }
    }
    fun deleteData(person: HistoryItemSearch.SearchHistory){
        viewModelScope.launch {
            repository.deleteData(person)
        }
    }

    fun searchDatabase(text : String) : LiveData<List<HistoryItemSearch.SearchHistory>> {
        return repository.searchDatabase(text).asLiveData()
    }
    fun deleteAllData(){
        viewModelScope.launch {
            repository.deleteAllData()
        }
    }
}