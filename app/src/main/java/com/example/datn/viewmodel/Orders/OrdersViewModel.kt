package com.example.datn.viewmodel.Orders

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.datn.data.dataresult.ResponseResult
import com.example.datn.data.dataresult.ResultMessage
import com.example.datn.data.dataresult.orders.ResultOrders
import com.example.datn.repository.repositoryProduct
import com.example.datn.utils.Extension.ErrorBodyMessage.getErrorBodyMessage
import com.example.datn.utils.network.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class OrdersViewModel(private val repositoryProduct: repositoryProduct) : ViewModel() {
    private val _isLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _resultOrderPending : MutableLiveData<ResponseResult<ResultOrders>> = MutableLiveData()
    val resultOrderPending : LiveData<ResponseResult<ResultOrders>> get() =  _resultOrderPending

    private val _resultDeleteOrder : MutableLiveData<ResponseResult<ResultMessage>> = MutableLiveData()
    val resultDeleteOrder : LiveData<ResponseResult<ResultMessage>> get() =  _resultDeleteOrder

    private val _resultOrderPacking : MutableLiveData<ResponseResult<ResultOrders>> = MutableLiveData()
    val resultOrderPacking : LiveData<ResponseResult<ResultOrders>> get() =  _resultOrderPacking

    private val _resultAllOrderShipping : MutableLiveData<ResponseResult<ResultOrders>> = MutableLiveData()
    val resultAllOrderShipping : LiveData<ResponseResult<ResultOrders>> get() =  _resultAllOrderShipping

    private val _resultOrderOrders : MutableLiveData<ResponseResult<ResultOrders>> = MutableLiveData()
    val resultOrderOrders : LiveData<ResponseResult<ResultOrders>> get() =  _resultOrderOrders

    private val _resultOrderReceived : MutableLiveData<ResponseResult<ResultOrders>> = MutableLiveData()
    val resultOrderReceived : LiveData<ResponseResult<ResultOrders>> get() =  _resultOrderReceived

    private val _resultOrderCancelled : MutableLiveData<ResponseResult<ResultOrders>> = MutableLiveData()
    val resultOrderCancelled : LiveData<ResponseResult<ResultOrders>> get() =  _resultOrderCancelled

     fun getAllOrderPacking() {
         viewModelScope.launch(Dispatchers.IO) {
             try {
                 _isLoading.postValue(true)
                 val response = repositoryProduct.getAllOrderPacking()
                 if (response.isSuccessful) {
                     val resultMessage = response.body()!!
                     _resultOrderPacking.postValue(ResponseResult.Success(resultMessage))
                 } else {
                     val errorBodyMessage = response.getErrorBodyMessage()
                     val finalErrorMessage = if (errorBodyMessage != "Unknown error") errorBodyMessage else "Error"
                     _resultOrderPacking.postValue(ResponseResult.Error(finalErrorMessage))
                 }
             } catch (e: IOException) {
                 _resultOrderPacking.postValue(ResponseResult.Error("Network connection error!"))
             } catch (e: HttpException) {
                 _resultOrderPacking.postValue(ResponseResult.Error("Error HTTP: ${e.message}"))
             } catch (e: Exception) {
                 _resultOrderPacking.postValue(ResponseResult.Error("An unknown error has occurred!"))
             }finally {
                 _isLoading.postValue(false)
             }
         }
    }



     fun getAllOrderShipping() {
         viewModelScope.launch(Dispatchers.IO) {
             try {
                 _isLoading.postValue(true)
                 val response = repositoryProduct.getAllOrderShipping()
                 if (response.isSuccessful) {
                     val resultMessage = response.body()!!
                     _resultAllOrderShipping.postValue(ResponseResult.Success(resultMessage))
                 } else {
                     val errorBodyMessage = response.getErrorBodyMessage()
                     val finalErrorMessage = if (errorBodyMessage != "Unknown error") errorBodyMessage else "Error"
                     _resultAllOrderShipping.postValue(ResponseResult.Error(finalErrorMessage))
                 }
             } catch (e: IOException) {
                 _resultAllOrderShipping.postValue(ResponseResult.Error("Network connection error!"))
             } catch (e: HttpException) {
                 _resultAllOrderShipping.postValue(ResponseResult.Error("Error HTTP: ${e.message}"))
             } catch (e: Exception) {
                 _resultAllOrderShipping.postValue(ResponseResult.Error("An unknown error has occurred!"))
             }finally {
                 _isLoading.postValue(false)
             }
         }
    }



     fun getAllOrderDelivering() {
         viewModelScope.launch(Dispatchers.IO) {
             try {
                 _isLoading.postValue(true)
                 val response = repositoryProduct.getAllOrderDelivering()
                 if (response.isSuccessful) {
                     val resultMessage = response.body()!!
                     _resultOrderOrders.postValue(ResponseResult.Success(resultMessage))
                 } else {
                     val errorBodyMessage = response.getErrorBodyMessage()
                     val finalErrorMessage = if (errorBodyMessage != "Unknown error") errorBodyMessage else "Error"
                     _resultOrderOrders.postValue(ResponseResult.Error(finalErrorMessage))
                 }
             } catch (e: IOException) {
                 _resultOrderOrders.postValue(ResponseResult.Error("Network connection error!"))
             } catch (e: HttpException) {
                 _resultOrderOrders.postValue(ResponseResult.Error("Error HTTP: ${e.message}"))
             } catch (e: Exception) {
                 _resultOrderOrders.postValue(ResponseResult.Error("An unknown error has occurred!"))
             }finally {
                 _isLoading.postValue(false)
             }
         }
    }



     fun getAllOrderReceived()  {
         viewModelScope.launch(Dispatchers.IO) {
             try {
                 _isLoading.postValue(true)
                 val response = repositoryProduct.getAllOrderReceived()
                 if (response.isSuccessful) {
                     val resultMessage = response.body()!!
                     _resultOrderReceived.postValue(ResponseResult.Success(resultMessage))
                 } else {
                     val errorBodyMessage = response.getErrorBodyMessage()
                     val finalErrorMessage = if (errorBodyMessage != "Unknown error") errorBodyMessage else "Error"
                     _resultOrderReceived.postValue(ResponseResult.Error(finalErrorMessage))
                 }
             } catch (e: IOException) {
                 _resultOrderReceived.postValue(ResponseResult.Error("Network connection error!"))
             } catch (e: HttpException) {
                 _resultOrderReceived.postValue(ResponseResult.Error("Error HTTP: ${e.message}"))
             } catch (e: Exception) {
                 _resultOrderReceived.postValue(ResponseResult.Error("An unknown error has occurred!"))
             }finally {
                 _isLoading.postValue(false)
             }
         }
    }



     fun getAllOrderCancelled()  {
         viewModelScope.launch(Dispatchers.IO) {
             try {
                 _isLoading.postValue(true)
                 val response = repositoryProduct.getAllOrderCancelled()
                 if (response.isSuccessful) {
                     val resultMessage = response.body()!!
                     _resultOrderCancelled.postValue(ResponseResult.Success(resultMessage))
                 } else {
                     val errorBodyMessage = response.getErrorBodyMessage()
                     val finalErrorMessage = if (errorBodyMessage != "Unknown error") errorBodyMessage else "Error"
                     _resultOrderCancelled.postValue(ResponseResult.Error(finalErrorMessage))
                 }
             } catch (e: IOException) {
                 _resultOrderCancelled.postValue(ResponseResult.Error("Network connection error!"))
             } catch (e: HttpException) {
                 _resultOrderCancelled.postValue(ResponseResult.Error("Error HTTP: ${e.message}"))
             } catch (e: Exception) {
                 _resultOrderCancelled.postValue(ResponseResult.Error("An unknown error has occurred!"))
             }finally {
                 _isLoading.postValue(false)
             }
         }
    }




    fun deleteOrder(id : Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _isLoading.postValue(true)
                val response = repositoryProduct.deleteOrder(id)
                if (response.isSuccessful) {
                    val resultMessage = response.body()!!
                    _resultDeleteOrder.postValue(ResponseResult.Success(resultMessage))
                } else {
                    val errorBodyMessage = response.getErrorBodyMessage()
                    val finalErrorMessage = if (errorBodyMessage != "Unknown error") errorBodyMessage else "Error"
                    _resultDeleteOrder.postValue(ResponseResult.Error(finalErrorMessage))
                }
            } catch (e: IOException) {
                _resultDeleteOrder.postValue(ResponseResult.Error("Network connection error!"))
            } catch (e: HttpException) {
                _resultDeleteOrder.postValue(ResponseResult.Error("Error HTTP: ${e.message}"))
            } catch (e: Exception) {
                _resultDeleteOrder.postValue(ResponseResult.Error("An unknown error has occurred!"))
            }finally {
                _isLoading.postValue(false)
            }
        }
    }
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