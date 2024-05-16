package com.example.datn.viewmodel.Products

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.datn.data.dataresult.ResponseResult
import com.example.datn.data.dataresult.ResultMessage
import com.example.datn.data.dataresult.resultCart
import com.example.datn.data.model.AddressRequest
import com.example.datn.repository.repositoryProduct
import com.example.datn.utils.Extention.ErrorBodyMessage.getErrorBodyMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class OrderViewModel(private val repositoryProduct: repositoryProduct) : ViewModel() {
    private val _isLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _resultCheckout : MutableLiveData<ResponseResult<resultCart>> = MutableLiveData()
    val resultCheckout : LiveData<ResponseResult<resultCart>> get() =  _resultCheckout

    private val _resultCreateOrder : MutableLiveData<ResponseResult<ResultMessage>> = MutableLiveData()
    val resultCreateOrder : LiveData<ResponseResult<ResultMessage>> get() =  _resultCreateOrder



    fun createAddOrders(addressRequest: AddressRequest){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _isLoading.postValue(true)
                val response = repositoryProduct.createAddOrders(addressRequest)
                if (response.isSuccessful) {
                    val resultMessage = response.body()!!
                    _resultCreateOrder.postValue(ResponseResult.Success(resultMessage))
                } else {
                    val errorBodyMessage = response.getErrorBodyMessage()
                    val finalErrorMessage = if (errorBodyMessage != "Unknown error") errorBodyMessage else "Error"
                    _resultCreateOrder.postValue(ResponseResult.Error(finalErrorMessage))
                }
            } catch (e: IOException) {
                _resultCreateOrder.postValue(ResponseResult.Error("Network connection error!"))
            } catch (e: HttpException) {
                _resultCreateOrder.postValue(ResponseResult.Error("Error HTTP: ${e.message}"))
            } catch (e: Exception) {
                _resultCreateOrder.postValue(ResponseResult.Error("An unknown error has occurred!"))
            }finally {
                _isLoading.postValue(false)
            }
        }
    }


    fun checkoutOrders() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _isLoading.postValue(true)
                val response = repositoryProduct.checkoutOrders()
                if (response.isSuccessful) {
                    val resultMessage = response.body()!!
                    _resultCheckout.postValue(ResponseResult.Success(resultMessage))
                } else {
                    val errorBodyMessage = response.getErrorBodyMessage()
                    val finalErrorMessage = if (errorBodyMessage != "Unknown error") errorBodyMessage else "Error"
                    _resultCheckout.postValue(ResponseResult.Error(finalErrorMessage))
                }
            } catch (e: IOException) {
                _resultCheckout.postValue(ResponseResult.Error("Network connection error!"))
            } catch (e: HttpException) {
                _resultCheckout.postValue(ResponseResult.Error("Error HTTP: ${e.message}"))
            } catch (e: Exception) {
                _resultCheckout.postValue(ResponseResult.Error("An unknown error has occurred!"))
            }finally {
                _isLoading.postValue(false)
            }
        }
    }
}