package com.example.datn.viewmodel.Orders

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.datn.data.dataresult.ResponseResult
import com.example.datn.data.dataresult.ResultMessage
import com.example.datn.data.dataresult.apiAddress.resultHuyen
import com.example.datn.data.dataresult.apiAddress.resultListAddress
import com.example.datn.data.dataresult.apiAddress.resultTInh
import com.example.datn.data.dataresult.apiAddress.resultXa
import com.example.datn.data.model.addAddress
import com.example.datn.repository.repositoryProduct
import com.example.datn.utils.Extension.ErrorBodyMessage.getErrorBodyMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class AddressesViewModel(private val repositoryProduct: repositoryProduct) : ViewModel() {

    private val _resultTinh : MutableLiveData<ResponseResult<resultTInh>> = MutableLiveData()
     val resultTinh : LiveData<ResponseResult<resultTInh>> get() =  _resultTinh

    private val _resultHuyen : MutableLiveData<ResponseResult<resultHuyen>> = MutableLiveData()
    val resultHuyen : LiveData<ResponseResult<resultHuyen>> get() =  _resultHuyen

    private val _resultXa : MutableLiveData<ResponseResult<resultXa>> = MutableLiveData()
    val resultXa : LiveData<ResponseResult<resultXa>> get() =  _resultXa

    private val _resultAdd : MutableLiveData<ResponseResult<ResultMessage>> = MutableLiveData()
    val resultAdd : LiveData<ResponseResult<ResultMessage>> get() =  _resultAdd

    private val _resultAll : MutableLiveData<ResponseResult<resultListAddress>> = MutableLiveData()
    val resultAll : LiveData<ResponseResult<resultListAddress>> get() =  _resultAll



    private val _isLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading



    fun getAllAddress(){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _isLoading.postValue(true)
                val response = repositoryProduct.getAllAddress()
                if (response.isSuccessful) {
                    val resultBody = response.body()!!
                    Log.e("MAIN",resultBody.toString())
                    _resultAll.postValue(ResponseResult.Success(resultBody))
                } else {
                    val errorBodyMessage = response.getErrorBodyMessage()
                    val finalErrorMessage = if (errorBodyMessage != "Unknown error") errorBodyMessage else "Error"
                    _resultAll.postValue(ResponseResult.Error(finalErrorMessage))
                }
            } catch (e: IOException) {
                _resultAll.postValue(ResponseResult.Error("Network connection error!"))
            } catch (e: HttpException) {
                _resultAll.postValue(ResponseResult.Error("Error HTTP: ${e.message}"))
            } catch (e: Exception) {
                _resultAll.postValue(ResponseResult.Error("An unknown error has occurred!"))
            }finally {
                _isLoading.postValue(false)
            }
        }
    }
    fun addAddressUser(address: addAddress){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _isLoading.postValue(true)
                val response = repositoryProduct.addAddressUser(address)
                if (response.isSuccessful) {
                    val resultBody = response.body()!!
                    Log.e("MAIN",resultBody.toString())
                    _resultAdd.postValue(ResponseResult.Success(resultBody))
                } else {
                    val errorBodyMessage = response.getErrorBodyMessage()
                    val finalErrorMessage = if (errorBodyMessage != "Unknown error") errorBodyMessage else "Error"
                    _resultAdd.postValue(ResponseResult.Error(finalErrorMessage))
                }
            } catch (e: IOException) {
                _resultAdd.postValue(ResponseResult.Error("Network connection error!"))
            } catch (e: HttpException) {
                _resultAdd.postValue(ResponseResult.Error("Error HTTP: ${e.message}"))
            } catch (e: Exception) {
                _resultAdd.postValue(ResponseResult.Error("An unknown error has occurred!"))
            }finally {
                _isLoading.postValue(false)
            }
        }
    }
    fun getWards(districtId : String){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repositoryProduct.getWardsXa(districtId)
                if (response.isSuccessful) {
                    val resultBody = response.body()!!
                    Log.e("MAIN",resultBody.toString())
                    _resultXa.postValue(ResponseResult.Success(resultBody))
                } else {
                    val errorBodyMessage = response.getErrorBodyMessage()
                    val finalErrorMessage = if (errorBodyMessage != "Unknown error") errorBodyMessage else "Error"
                    _resultXa.postValue(ResponseResult.Error(finalErrorMessage))
                }
            } catch (e: IOException) {
                _resultXa.postValue(ResponseResult.Error("Network connection error!"))
            } catch (e: HttpException) {
                _resultXa.postValue(ResponseResult.Error("Error HTTP: ${e.message}"))
            } catch (e: Exception) {
                _resultXa.postValue(ResponseResult.Error("An unknown error has occurred!"))
            }
        }
    }
    fun getAllHuyen(idTinh : String){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repositoryProduct.getDistricts(idTinh)
                if (response.isSuccessful) {
                    val resultBody = response.body()!!
                    _resultHuyen.postValue(ResponseResult.Success(resultBody))
                } else {
                    val errorBodyMessage = response.getErrorBodyMessage()
                    val finalErrorMessage = if (errorBodyMessage != "Unknown error") errorBodyMessage else "Error"
                    _resultHuyen.postValue(ResponseResult.Error(finalErrorMessage))
                }
            } catch (e: IOException) {
                _resultHuyen.postValue(ResponseResult.Error("Network connection error!"))
            } catch (e: HttpException) {
                _resultHuyen.postValue(ResponseResult.Error("Error HTTP: ${e.message}"))
            } catch (e: Exception) {
                _resultHuyen.postValue(ResponseResult.Error("An unknown error has occurred!"))
            }
        }
    }

    fun getAllTinh(){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repositoryProduct.getProvinces()
                if (response.isSuccessful) {
                    val resultBody = response.body()!!
                    _resultTinh.postValue(ResponseResult.Success(resultBody))
                } else {
                    val errorBodyMessage = response.getErrorBodyMessage()
                    val finalErrorMessage = if (errorBodyMessage != "Unknown error") errorBodyMessage else "Error"
                    _resultTinh.postValue(ResponseResult.Error(finalErrorMessage))
                }
            } catch (e: IOException) {
                _resultTinh.postValue(ResponseResult.Error("Network connection error!"))
            } catch (e: HttpException) {
                _resultTinh.postValue(ResponseResult.Error("Error HTTP: ${e.message}"))
            } catch (e: Exception) {
                _resultTinh.postValue(ResponseResult.Error("An unknown error has occurred!"))
            }
        }
    }
}