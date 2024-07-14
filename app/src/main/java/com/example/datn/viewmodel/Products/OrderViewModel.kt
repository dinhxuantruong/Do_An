package com.example.datn.viewmodel.Products

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.datn.data.dataresult.ResponseResult
import com.example.datn.data.dataresult.ResultMessage
import com.example.datn.data.dataresult.UserX
import com.example.datn.data.dataresult.apiAddress.resultDefault
import com.example.datn.data.dataresult.orders.Order
import com.example.datn.data.dataresult.resultCart
import com.example.datn.data.dataresult.resultOrderDetails
import com.example.datn.data.model.AddressRequest
import com.example.datn.data.model.dataVoucher
import com.example.datn.repository.repositoryProduct
import com.example.datn.utils.Extension.ErrorBodyMessage.getErrorBodyMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException
import java.io.IOException

class OrderViewModel(private val repositoryProduct: repositoryProduct) : ViewModel() {
    private val _isLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _resultCheckout : MutableLiveData<ResponseResult<resultCart>> = MutableLiveData()
    val resultCheckout : LiveData<ResponseResult<resultCart>> get() =  _resultCheckout

    private val _resultCreateOrder : MutableLiveData<ResponseResult<ResultMessage>> = MutableLiveData()
    val resultCreateOrder : LiveData<ResponseResult<ResultMessage>> get() =  _resultCreateOrder

    private val _resultDefaultAddress : MutableLiveData<ResponseResult<resultDefault>> = MutableLiveData()
    val resultDefaultAddress : LiveData<ResponseResult<resultDefault>> get() =  _resultDefaultAddress

    private val _resultDetailAddress : MutableLiveData<ResponseResult<resultDefault>> = MutableLiveData()
    val resultDetailAddress : LiveData<ResponseResult<resultDefault>> get() =  _resultDetailAddress
    private val _resultDetailsOrder : MutableLiveData<ResponseResult<resultOrderDetails>> = MutableLiveData()
    val resultDetailsOrder : LiveData<ResponseResult<resultOrderDetails>> get() = _resultDetailsOrder

    private val _resultLogout : MutableLiveData<ResponseResult<ResultMessage>> = MutableLiveData()
    val resultLogout : LiveData<ResponseResult<ResultMessage>> get() = _resultLogout

    private val _resultProfile :MutableLiveData<ResponseResult<UserX>> = MutableLiveData()
    val resultProfile : LiveData<ResponseResult<UserX>> get() = _resultProfile

    private val _resultUpdateProfile : MutableLiveData<ResponseResult<ResultMessage>> = MutableLiveData()
    val resultUpdateProfile : LiveData<ResponseResult<ResultMessage>> get() = _resultUpdateProfile

    private val _resultCheckVoucher : MutableLiveData<ResponseResult<Order>> = MutableLiveData()
    val resultCheckVoucher : LiveData<ResponseResult<Order>> get() = _resultCheckVoucher

    //Change status zalopay
    private val _resultStatusZaloPay : MutableLiveData<ResponseResult<ResultMessage>> = MutableLiveData()
    val resultStatusZaloPay : LiveData<ResponseResult<ResultMessage>> get() =  _resultStatusZaloPay

    //response delete order
    private val _resultOrderDelete : MutableLiveData<ResponseResult<ResultMessage>> = MutableLiveData()
    val resultOrderDelete : LiveData<ResponseResult<ResultMessage>> get() = _resultOrderDelete

    fun deleteOrderAdmin(idOrder: Int){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _isLoading.postValue(true)
                val response = repositoryProduct.deleteOrderAdmin(idOrder)
                if (response.isSuccessful) {
                    val resultMessage = response.body()!!
                    _resultOrderDelete.postValue(ResponseResult.Success(resultMessage))
                } else {
                    val errorBodyMessage = response.getErrorBodyMessage()
                    val finalErrorMessage = if (errorBodyMessage != "Unknown error") errorBodyMessage else "Error"
                    _resultOrderDelete.postValue(ResponseResult.Error(finalErrorMessage))
                }
            } catch (e: IOException) {
                _resultOrderDelete.postValue(ResponseResult.Error("Network connection error!"))
            } catch (e: HttpException) {
                _resultOrderDelete.postValue(ResponseResult.Error("Error HTTP: ${e.message}"))
            } catch (e: Exception) {
                _resultOrderDelete.postValue(ResponseResult.Error("An unknown error has occurred!"))
            }finally {
                _isLoading.postValue(false)
            }
        }
    }
    fun changeStatusZaloPay( idOrder: Int, bank_transaction_id : String){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _isLoading.postValue(true)
                val response = repositoryProduct.changeStatusZaloPay(idOrder, bank_transaction_id)
                if (response.isSuccessful) {
                    val resultMessage = response.body()!!
                    _resultStatusZaloPay.postValue(ResponseResult.Success(resultMessage))
                } else {
                    val errorBodyMessage = response.getErrorBodyMessage()
                    val finalErrorMessage = if (errorBodyMessage != "Unknown error") errorBodyMessage else "Error"
                    _resultStatusZaloPay.postValue(ResponseResult.Error(finalErrorMessage))
                }
            } catch (e: IOException) {
                _resultStatusZaloPay.postValue(ResponseResult.Error("Network connection error!"))
            } catch (e: HttpException) {
                _resultStatusZaloPay.postValue(ResponseResult.Error("Error HTTP: ${e.message}"))
            } catch (e: Exception) {
                _resultStatusZaloPay.postValue(ResponseResult.Error("An unknown error has occurred!"))
            }finally {
                _isLoading.postValue(false)
            }
        }
    }



    fun testVoucher( dataVoucher : dataVoucher){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _isLoading.postValue(true)
                val response = repositoryProduct.testVoucher(dataVoucher)
                if (response.isSuccessful) {
                    val resultMessage = response.body()!!
                    _resultCheckVoucher.postValue(ResponseResult.Success(resultMessage))
                } else {
                    val errorBodyMessage = response.getErrorBodyMessage()
                    val finalErrorMessage = if (errorBodyMessage != "Unknown error") errorBodyMessage else "Error"
                    _resultCheckVoucher.postValue(ResponseResult.Error(finalErrorMessage))
                }
            } catch (e: IOException) {
                _resultCheckVoucher.postValue(ResponseResult.Error("Network connection error!"))
            } catch (e: HttpException) {
                _resultCheckVoucher.postValue(ResponseResult.Error("Error HTTP: ${e.message}"))
            } catch (e: Exception) {
                _resultCheckVoucher.postValue(ResponseResult.Error("An unknown error has occurred!"))
            }finally {
                _isLoading.postValue(false)
            }
        }
    }
    fun updateProFileUser(
        name: RequestBody,
        profession: RequestBody,
        phone: RequestBody,
        profile_photo: MultipartBody.Part?
        ){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _isLoading.postValue(true)
                val response = repositoryProduct.updateProFile(name,profession,phone,profile_photo)
                if (response.isSuccessful) {
                    val resultMessage = response.body()!!
                    _resultUpdateProfile.postValue(ResponseResult.Success(resultMessage))
                } else {
                    val errorBodyMessage = response.getErrorBodyMessage()
                    val finalErrorMessage = if (errorBodyMessage != "Unknown error") errorBodyMessage else "Error"
                    _resultUpdateProfile.postValue(ResponseResult.Error(finalErrorMessage))
                }
            } catch (e: IOException) {
                _resultUpdateProfile.postValue(ResponseResult.Error("Network connection error!"))
            } catch (e: HttpException) {
                _resultUpdateProfile.postValue(ResponseResult.Error("Error HTTP: ${e.message}"))
            } catch (e: Exception) {
                _resultUpdateProfile.postValue(ResponseResult.Error("An unknown error has occurred!"))
            }finally {
                _isLoading.postValue(false)
            }
        }
    }
    fun  getProfileUser(){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _isLoading.postValue(true)
                val response = repositoryProduct.getProfileUser()
                if (response.isSuccessful) {
                    val resultMessage = response.body()!!
                    _resultProfile.postValue(ResponseResult.Success(resultMessage))
                } else {
                    val errorBodyMessage = response.getErrorBodyMessage()
                    val finalErrorMessage = if (errorBodyMessage != "Unknown error") errorBodyMessage else "Error"
                    _resultProfile.postValue(ResponseResult.Error(finalErrorMessage))
                }
            } catch (e: IOException) {
                _resultProfile.postValue(ResponseResult.Error("Network connection error!"))
            } catch (e: HttpException) {
                _resultProfile.postValue(ResponseResult.Error("Error HTTP: ${e.message}"))
            } catch (e: Exception) {
                _resultProfile.postValue(ResponseResult.Error("An unknown error has occurred!"))
            }finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun logout() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _isLoading.postValue(true)
                val response = repositoryProduct.logout()
                if (response.isSuccessful) {
                    val resultMessage = response.body()!!
                    _resultLogout.postValue(ResponseResult.Success(resultMessage))
                } else {
                    val errorBodyMessage = response.getErrorBodyMessage()
                    val finalErrorMessage = if (errorBodyMessage != "Unknown error") errorBodyMessage else "Error"
                    _resultLogout.postValue(ResponseResult.Error(finalErrorMessage))
                }
            } catch (e: IOException) {
                _resultLogout.postValue(ResponseResult.Error("Network connection error!"))
            } catch (e: HttpException) {
                _resultLogout.postValue(ResponseResult.Error("Error HTTP: ${e.message}"))
            } catch (e: Exception) {
                _resultLogout.postValue(ResponseResult.Error("An unknown error has occurred!"))
            }finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun getDetailsOrder(id : Int){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _isLoading.postValue(true)
                val response = repositoryProduct.getDetailsOrder(id)
                if (response.isSuccessful) {
                    val resultMessage = response.body()!!
                    _resultDetailsOrder.postValue(ResponseResult.Success(resultMessage))
                } else {
                    val errorBodyMessage = response.getErrorBodyMessage()
                    val finalErrorMessage = if (errorBodyMessage != "Unknown error") errorBodyMessage else "Error"
                    _resultDetailsOrder.postValue(ResponseResult.Error(finalErrorMessage))
                }
            } catch (e: IOException) {
                _resultDetailsOrder.postValue(ResponseResult.Error("Network connection error!"))
            } catch (e: HttpException) {
                _resultDetailsOrder.postValue(ResponseResult.Error("Error HTTP: ${e.message}"))
            } catch (e: Exception) {
                _resultDetailsOrder.postValue(ResponseResult.Error("An unknown error has occurred!"))
            }finally {
                _isLoading.postValue(false)
            }
        }
    }
    fun getDetailAddress(idAddress : String?){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _isLoading.postValue(true)
                val response = repositoryProduct.getDetailAddress(idAddress)
                if (response.isSuccessful) {
                    val resultBody = response.body()!!
                    Log.e("MAIN",resultBody.toString())
                    _resultDetailAddress.postValue(ResponseResult.Success(resultBody))
                } else {
                    val errorBodyMessage = response.getErrorBodyMessage()
                    val finalErrorMessage = if (errorBodyMessage != "Unknown error") errorBodyMessage else "Error"
                    _resultDetailAddress.postValue(ResponseResult.Error(finalErrorMessage))
                }
            } catch (e: IOException) {
                _resultDetailAddress.postValue(ResponseResult.Error("Network connection error!"))
            } catch (e: HttpException) {
                _resultDetailAddress.postValue(ResponseResult.Error("Error HTTP: ${e.message}"))
            } catch (e: Exception) {
                _resultDetailAddress.postValue(ResponseResult.Error("An unknown error has occurred!"))
            }finally {
                _isLoading.postValue(false)
            }
        }
    }
    fun getDefaultAddress(){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _isLoading.postValue(true)
                val response = repositoryProduct.getDefaultAddress()
                if (response.isSuccessful) {
                    val resultBody = response.body()!!
                    Log.e("MAIN",resultBody.toString())
                    _resultDefaultAddress.postValue(ResponseResult.Success(resultBody))
                } else {
                    val errorBodyMessage = response.getErrorBodyMessage()
                    val finalErrorMessage = if (errorBodyMessage != "Unknown error") errorBodyMessage else "Error"
                    _resultDefaultAddress.postValue(ResponseResult.Error(finalErrorMessage))
                }
            } catch (e: IOException) {
                _resultDefaultAddress.postValue(ResponseResult.Error("Network connection error!"))
            } catch (e: HttpException) {
                _resultDefaultAddress.postValue(ResponseResult.Error("Error HTTP: ${e.message}"))
            } catch (e: Exception) {
                _resultDefaultAddress.postValue(ResponseResult.Error("An unknown error has occurred!"))
            }finally {
                _isLoading.postValue(false)
            }
        }
    }
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