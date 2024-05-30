package com.example.datn.viewmodel.Products

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.datn.data.dataresult.ProductType
import com.example.datn.data.dataresult.ResponseResult
import com.example.datn.data.dataresult.ResultMessage
import com.example.datn.data.dataresult.ResultProductDetail
import com.example.datn.data.model.addCart
import com.example.datn.repository.repositoryProduct
import com.example.datn.utils.Extension.ErrorBodyMessage.getErrorBodyMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class ViewModelDetailProduct(private val repositoryProduct: repositoryProduct) : ViewModel() {

    private val _resultDetail: MutableLiveData<ResponseResult<ResultProductDetail>> =
        MutableLiveData()
    val resultDetail: LiveData<ResponseResult<ResultProductDetail>> get() = _resultDetail

    private val _resultAddFavorite: MutableLiveData<ResponseResult<ResultMessage>> =
        MutableLiveData()
    val resultAddFavorite: LiveData<ResponseResult<ResultMessage>> get() = _resultAddFavorite

    private val _resultProductSame: MutableLiveData<ResponseResult<ProductType>> = MutableLiveData()
    val resultProductSame: LiveData<ResponseResult<ProductType>> get() = _resultProductSame

    private val _resultAddCart: MutableLiveData<ResponseResult<ResultMessage>> = MutableLiveData()
    val resultAddCart: LiveData<ResponseResult<ResultMessage>> get() = _resultAddCart

    private val _isLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading
    private val _resultGetCartCount : MutableLiveData<ResponseResult<ResultMessage>> = MutableLiveData()
    val resultGetCartCount : LiveData<ResponseResult<ResultMessage>> get() =  _resultGetCartCount



    fun getCartCount(){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repositoryProduct.getCartCount()
                if (response.isSuccessful) {
                    val logout = response.body()!!
                    _resultGetCartCount.postValue(ResponseResult.Success(logout))
                } else {
                    val errorMessage = "Logout unsuccessful: ${response.message()}"
                    _resultGetCartCount.postValue(ResponseResult.Error(errorMessage))
                }
            } catch (e: IOException) {
                _resultGetCartCount.postValue(ResponseResult.Error("Network connection error2!"))
            } catch (e: HttpException) {
                _resultGetCartCount.postValue(ResponseResult.Error("Error HTTP: ${e.message}"))
            } catch (e: Exception) {
                _resultGetCartCount.postValue(ResponseResult.Error("An unknown error has occurred!"))
            }
        }
    }
    fun addToCart(cart : addCart) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _isLoading.postValue(true)
                val response = repositoryProduct.addToCart(cart)
                if (response.isSuccessful) {
                    val resultMessage = response.body()!!
                    _resultAddCart.postValue(ResponseResult.Success(resultMessage))
                } else {
                    val errorMessage = "Add favorite does not exist : ${response.message()}"
                    val errorBodyMessage = response.getErrorBodyMessage()
                    val finalErrorMessage = if (errorBodyMessage != "Unknown error") errorBodyMessage else errorMessage
                    _resultAddCart.postValue(ResponseResult.Error(finalErrorMessage))
                }
            } catch (e: IOException) {
                _resultAddCart.postValue(ResponseResult.Error("Network connection error!"))
            } catch (e: HttpException) {
                _resultAddCart.postValue(ResponseResult.Error("Error HTTP: ${e.message}"))
            } catch (e: Exception) {
                _resultAddCart.postValue(ResponseResult.Error("An unknown error has occurred!"))
            }finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun addFavorite(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repositoryProduct.addFavorite(id)
                if (response.isSuccessful) {
                    val resultMessage = response.body()!!
                    _resultAddFavorite.postValue(ResponseResult.Success(resultMessage))
                } else {
                    val errorMessage = "Add favorite does not exist : ${response.message()}"
                    _resultAddFavorite.postValue(ResponseResult.Error(errorMessage))
                }
            } catch (e: IOException) {
                _resultAddFavorite.postValue(ResponseResult.Error("Network connection error!"))
            } catch (e: HttpException) {
                _resultAddFavorite.postValue(ResponseResult.Error("Error HTTP: ${e.message}"))
            } catch (e: Exception) {
                _resultAddFavorite.postValue(ResponseResult.Error("An unknown error has occurred!"))
            }
        }
    }


    private suspend fun getProductSame(category : Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repositoryProduct.searchByCategory(category)
                if (response.isSuccessful) {
                    val resultMessage = response.body()!!
                    _resultProductSame.postValue(ResponseResult.Success(resultMessage))
                } else {
                    val errorMessage = "Category product does not exist : ${response.message()}"
                    _resultProductSame.postValue(ResponseResult.Error(errorMessage))
                }
            } catch (e: IOException) {
                _resultProductSame.postValue(ResponseResult.Error("Network connection error!"))
            } catch (e: HttpException) {
                _resultProductSame.postValue(ResponseResult.Error("Error HTTP: ${e.message}"))
            } catch (e: Exception) {
                _resultProductSame.postValue(ResponseResult.Error("An unknown error has occurred!"))
            }
        }
    }

      fun getDetailProduct(id : Int) {
         viewModelScope.launch(Dispatchers.IO) {
             try {
                 val response = repositoryProduct.getDetailProduct(id)
                 if (response.isSuccessful) {
                     val result = response.body()!!
                     _resultDetail.postValue(ResponseResult.Success(result))
                     val categoryId = result.ProductType.id_category
                     getProductSame(categoryId)
                 } else {
                     val errorMessage = "Detail product does not exist : ${response.message()}"
                     _resultDetail.postValue(ResponseResult.Error(errorMessage))
                 }
             } catch (e: IOException) {
                 _resultDetail.postValue(ResponseResult.Error("Network connection error!"))
             } catch (e: HttpException) {
                 _resultDetail.postValue(ResponseResult.Error("Error HTTP: ${e.message}"))
             } catch (e: Exception) {
                 _resultDetail.postValue(ResponseResult.Error("An unknown error has occurred!"))
             }
         }
     }
}