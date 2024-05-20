package com.example.datn.viewmodel.Orders

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.datn.data.dataresult.ResponseResult
import com.example.datn.data.dataresult.orders.ResultOrders
import com.example.datn.repository.repositoryProduct
import com.example.datn.utils.Extension.ErrorBodyMessage.getErrorBodyMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class OrdersViewModel(private val repositoryProduct: repositoryProduct) : ViewModel() {
    private val _isLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _resultOrderPending : MutableLiveData<ResponseResult<ResultOrders>> = MutableLiveData()
    val resultOrderPending : LiveData<ResponseResult<ResultOrders>> get() =  _resultOrderPending

    fun getOrderPending(){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _isLoading.postValue(true)
                val response = repositoryProduct.getAllOrdersPending()
                if (response.isSuccessful) {
                    val resultMessage = response.body()!!
                    _resultOrderPending.postValue(ResponseResult.Success(resultMessage))
                } else {
                    val errorBodyMessage = response.getErrorBodyMessage()
                    val finalErrorMessage = if (errorBodyMessage != "Unknown error") errorBodyMessage else "Error"
                    _resultOrderPending.postValue(ResponseResult.Error(finalErrorMessage))
                }
            } catch (e: IOException) {
                _resultOrderPending.postValue(ResponseResult.Error("Network connection error!"))
            } catch (e: HttpException) {
                _resultOrderPending.postValue(ResponseResult.Error("Error HTTP: ${e.message}"))
            } catch (e: Exception) {
                _resultOrderPending.postValue(ResponseResult.Error("An unknown error has occurred!"))
            }finally {
                _isLoading.postValue(false)
            }
        }
    }
}