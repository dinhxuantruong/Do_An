package com.example.datn.viewmodel.Products

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.datn.data.dataresult.ResponseResult
import com.example.datn.data.dataresult.ResultMessage
import com.example.datn.data.dataresult.resultCart
import com.example.datn.repository.repositoryProduct
import com.example.datn.utils.Extention.ErrorBodyMessage.getErrorBodyMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class CartViewModel(private val repositoryProduct: repositoryProduct) : ViewModel() {

    private val _resultAllCart : MutableLiveData<ResponseResult<resultCart>> = MutableLiveData()
    val resultCart : LiveData<ResponseResult<resultCart>> get() =  _resultAllCart

    private val _resultChangeCart : MutableLiveData<ResponseResult<ResultMessage>> = MutableLiveData()
    val resultChangeCart : LiveData<ResponseResult<ResultMessage>> get() =  _resultChangeCart

    private val _resultAllBox : MutableLiveData<ResponseResult<ResultMessage>> = MutableLiveData()
    val resultAllBox : LiveData<ResponseResult<ResultMessage>> get() =  _resultAllBox

    private val _resultDeleteCheckBox : MutableLiveData<ResponseResult<ResultMessage>> = MutableLiveData()
    val resultDeleteCheckBox : LiveData<ResponseResult<ResultMessage>> get() =  _resultDeleteCheckBox

    private val _resultDeleteCart : MutableLiveData<ResponseResult<ResultMessage>> = MutableLiveData()
    val resultDeleteCart : LiveData<ResponseResult<ResultMessage>> get() =  _resultDeleteCart

    private val _resultDeleteItemCart : MutableLiveData<ResponseResult<ResultMessage>> = MutableLiveData()
    val resultDeleteItemCart : LiveData<ResponseResult<ResultMessage>> get() =  _resultDeleteItemCart

    private val _resultPlusItemCart : MutableLiveData<ResponseResult<ResultMessage>> = MutableLiveData()
    val resultPlusItemCart : LiveData<ResponseResult<ResultMessage>> get() =  _resultPlusItemCart


    private val _resultMinusItemCart : MutableLiveData<ResponseResult<ResultMessage>> = MutableLiveData()
    val resultMinusItemCart : LiveData<ResponseResult<ResultMessage>> get() =  _resultMinusItemCart

    private val _resultCheckStock : MutableLiveData<ResponseResult<ResultMessage>> = MutableLiveData()
    val resultCheckStock : LiveData<ResponseResult<ResultMessage>> get() =  _resultCheckStock


    private val _isLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading



    fun checkStockStatus(){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _isLoading.postValue(true)
                val response = repositoryProduct.checkStockStatus()
                if (response.isSuccessful) {
                    val resultMessage = response.body()!!
                    _resultCheckStock.postValue(ResponseResult.Success(resultMessage))
                } else {
                    val errorBodyMessage = response.getErrorBodyMessage()
                    val finalErrorMessage = if (errorBodyMessage != "Unknown error") errorBodyMessage else "Error"
                    _resultCheckStock.postValue(ResponseResult.Error(finalErrorMessage))
                }
            } catch (e: IOException) {
                _resultCheckStock.postValue(ResponseResult.Error("Network connection error!"))
            } catch (e: HttpException) {
                _resultCheckStock.postValue(ResponseResult.Error("Error HTTP: ${e.message}"))
            } catch (e: Exception) {
                _resultCheckStock.postValue(ResponseResult.Error("An unknown error has occurred!"))
            }finally {
                _isLoading.postValue(false)
            }
        }
    }
    fun minusItemCart(id : Int){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _isLoading.postValue(true)
                val response = repositoryProduct.minusItemCart(id)
                if (response.isSuccessful) {
                    val resultMessage = response.body()!!
                    _resultMinusItemCart.postValue(ResponseResult.Success(resultMessage))
                } else {
                    val errorBodyMessage = response.getErrorBodyMessage()
                    val finalErrorMessage = if (errorBodyMessage != "Unknown error") errorBodyMessage else "Error"
                    _resultMinusItemCart.postValue(ResponseResult.Error(finalErrorMessage))
                }
            } catch (e: IOException) {
                _resultMinusItemCart.postValue(ResponseResult.Error("Network connection error!"))
            } catch (e: HttpException) {
                _resultMinusItemCart.postValue(ResponseResult.Error("Error HTTP: ${e.message}"))
            } catch (e: Exception) {
                _resultMinusItemCart.postValue(ResponseResult.Error("An unknown error has occurred!"))
            }finally {
                _isLoading.postValue(false)
            }
        }
    }
    fun plusItemCart(id : Int){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _isLoading.postValue(true)
                val response = repositoryProduct.plusItemCart(id)
                if (response.isSuccessful) {
                    val resultMessage = response.body()!!
                    _resultPlusItemCart.postValue(ResponseResult.Success(resultMessage))
                } else {
                    val errorBodyMessage = response.getErrorBodyMessage()
                    val finalErrorMessage = if (errorBodyMessage != "Unknown error") errorBodyMessage else "Error"
                    _resultPlusItemCart.postValue(ResponseResult.Error(finalErrorMessage))
                }
            } catch (e: IOException) {
                _resultPlusItemCart.postValue(ResponseResult.Error("Network connection error!"))
            } catch (e: HttpException) {
                _resultPlusItemCart.postValue(ResponseResult.Error("Error HTTP: ${e.message}"))
            } catch (e: Exception) {
                _resultPlusItemCart.postValue(ResponseResult.Error("An unknown error has occurred!"))
            }finally {
                _isLoading.postValue(false)
            }
        }
    }
    fun deleteItemCart(id : Int){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _isLoading.postValue(true)
                val response = repositoryProduct.deleteItemCart(id)
                if (response.isSuccessful) {
                    val resultMessage = response.body()!!
                    _resultDeleteItemCart.postValue(ResponseResult.Success(resultMessage))
                } else {
                    val errorBodyMessage = response.getErrorBodyMessage()
                    val finalErrorMessage = if (errorBodyMessage != "Unknown error") errorBodyMessage else "Error"
                    _resultDeleteItemCart.postValue(ResponseResult.Error(finalErrorMessage))
                }
            } catch (e: IOException) {
                _resultDeleteItemCart.postValue(ResponseResult.Error("Network connection error!"))
            } catch (e: HttpException) {
                _resultDeleteItemCart.postValue(ResponseResult.Error("Error HTTP: ${e.message}"))
            } catch (e: Exception) {
                _resultDeleteItemCart.postValue(ResponseResult.Error("An unknown error has occurred!"))
            }finally {
                _isLoading.postValue(false)
            }
        }
    }
    fun deleteCart(){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _isLoading.postValue(true)
                val response = repositoryProduct.deleteCart()
                if (response.isSuccessful) {
                    val resultMessage = response.body()!!
                    _resultDeleteCart.postValue(ResponseResult.Success(resultMessage))
                } else {
                    val errorBodyMessage = response.getErrorBodyMessage()
                    val finalErrorMessage = if (errorBodyMessage != "Unknown error") errorBodyMessage else "Error"
                    _resultDeleteCart.postValue(ResponseResult.Error(finalErrorMessage))
                }
            } catch (e: IOException) {
                _resultDeleteCart.postValue(ResponseResult.Error("Network connection error!"))
            } catch (e: HttpException) {
                _resultDeleteCart.postValue(ResponseResult.Error("Error HTTP: ${e.message}"))
            } catch (e: Exception) {
                _resultDeleteCart.postValue(ResponseResult.Error("An unknown error has occurred!"))
            }finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun deleteCheckBoxAll(){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _isLoading.postValue(true)
                val response = repositoryProduct.deleteCheckBoxAll()
                if (response.isSuccessful) {
                    val resultMessage = response.body()!!
                    _resultDeleteCheckBox.postValue(ResponseResult.Success(resultMessage))
                } else {
                    val errorBodyMessage = response.getErrorBodyMessage()
                    val finalErrorMessage = if (errorBodyMessage != "Unknown error") errorBodyMessage else "Error"
                    _resultDeleteCheckBox.postValue(ResponseResult.Error(finalErrorMessage))
                }
            } catch (e: IOException) {
                _resultDeleteCheckBox.postValue(ResponseResult.Error("Network connection error!"))
            } catch (e: HttpException) {
                _resultDeleteCheckBox.postValue(ResponseResult.Error("Error HTTP: ${e.message}"))
            } catch (e: Exception) {
                _resultDeleteCheckBox.postValue(ResponseResult.Error("An unknown error has occurred!"))
            }finally {
                _isLoading.postValue(false)
            }
        }
    }
    fun allBox(){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _isLoading.postValue(true)
                val response = repositoryProduct.checkAllBox()
                if (response.isSuccessful) {
                    val resultMessage = response.body()!!
                    _resultAllBox.postValue(ResponseResult.Success(resultMessage))
                } else {
                    val errorBodyMessage = response.getErrorBodyMessage()
                    val finalErrorMessage = if (errorBodyMessage != "Unknown error") errorBodyMessage else "Error"
                    _resultAllBox.postValue(ResponseResult.Error(finalErrorMessage))
                }
            } catch (e: IOException) {
                _resultAllBox.postValue(ResponseResult.Error("Network connection error!"))
            } catch (e: HttpException) {
                _resultAllBox.postValue(ResponseResult.Error("Error HTTP: ${e.message}"))
            } catch (e: Exception) {
                _resultAllBox.postValue(ResponseResult.Error("An unknown error has occurred!"))
            }finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun getAllCart(){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _isLoading.postValue(true)
                val response = repositoryProduct.getAllCart()
                if (response.isSuccessful) {
                    val resultMessage = response.body()!!
                    _resultAllCart.postValue(ResponseResult.Success(resultMessage))
                } else {
                    val errorBodyMessage = response.getErrorBodyMessage()
                    val finalErrorMessage = if (errorBodyMessage != "Unknown error") errorBodyMessage else "Error"
                    _resultAllCart.postValue(ResponseResult.Error(finalErrorMessage))
                }
            } catch (e: IOException) {
                _resultAllCart.postValue(ResponseResult.Error("Network connection error!"))
            } catch (e: HttpException) {
                _resultAllCart.postValue(ResponseResult.Error("Error HTTP: ${e.message}"))
            } catch (e: Exception) {
                _resultAllCart.postValue(ResponseResult.Error("An unknown error has occurred!"))
            }finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun changeCart(cartId : Int){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _isLoading.postValue(true)
                val response = repositoryProduct.changeCart(cartId)
                if (response.isSuccessful) {
                    val resultMessage = response.body()!!
                    _resultChangeCart.postValue(ResponseResult.Success(resultMessage))
                } else {
                    val errorBodyMessage = response.getErrorBodyMessage()
                    val finalErrorMessage = if (errorBodyMessage != "Unknown error") errorBodyMessage else "Error"
                    _resultChangeCart.postValue(ResponseResult.Error(finalErrorMessage))
                }
            } catch (e: IOException) {
                _resultChangeCart.postValue(ResponseResult.Error("Network connection error!"))
            } catch (e: HttpException) {
                _resultChangeCart.postValue(ResponseResult.Error("Error HTTP: ${e.message}"))
            } catch (e: Exception) {
                _resultChangeCart.postValue(ResponseResult.Error("An unknown error has occurred!"))
            }finally {
                _isLoading.postValue(false)
            }
        }
    }
}