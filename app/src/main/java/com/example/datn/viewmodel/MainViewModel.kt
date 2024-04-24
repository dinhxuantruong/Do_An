package com.example.datn.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.datn.data.ResultMessage
import com.example.datn.repository.repositoryAuth
import com.example.datn.utils.DataResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class MainViewModel(private val repositoryAuth: repositoryAuth) : ViewModel() {
    private val _resultLogout : MutableLiveData<DataResult<ResultMessage>> = MutableLiveData()
    val resultLogout : LiveData<DataResult<ResultMessage>> get() = _resultLogout

    fun authLogout() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repositoryAuth.authLogout()
                if (response.isSuccessful) {
                    val logout = response.body()!!
                    _resultLogout.postValue(DataResult.Success(logout))
                } else {
                    val errorMessage = "Logout unsuccessful: ${response.message()}"
                    _resultLogout.postValue(DataResult.Error(errorMessage))
                }
            } catch (e: IOException) {
                _resultLogout.postValue(DataResult.Error("Network connection error2!"))
            } catch (e: HttpException) {
                _resultLogout.postValue(DataResult.Error("Error HTTP: ${e.message}"))
            } catch (e: Exception) {
                _resultLogout.postValue(DataResult.Error("An unknown error has occurred!"))
            }
        }
    }
}