package com.example.datn.viewmodel.Products

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.datn.data.ProductType
import com.example.datn.data.ResultMessage
import com.example.datn.data.ResultProductDetail
import com.example.datn.repository.repositoryProduct
import com.example.datn.utils.DataResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class ViewModelDetailProduct(private val repositoryProduct: repositoryProduct) : ViewModel() {

    private val _resultDetail : MutableLiveData<DataResult<ResultProductDetail>> = MutableLiveData()
    val resultDetail : LiveData<DataResult<ResultProductDetail>> get() = _resultDetail

    private val _resultAddFavorite : MutableLiveData<DataResult<ResultMessage>> = MutableLiveData()
    val resultAddFavorite : LiveData<DataResult<ResultMessage>> get() = _resultAddFavorite

    private val _resultProductSame : MutableLiveData<DataResult<ProductType>> = MutableLiveData()
    val resultProductSame : LiveData<DataResult<ProductType>> get() = _resultProductSame

    fun addFavorite(id : Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repositoryProduct.addFavorite(id)
                if (response.isSuccessful) {
                    val resultMessage = response.body()!!
                    _resultAddFavorite.postValue(DataResult.Success(resultMessage))
                } else {
                    val errorMessage = "Add favorite does not exist : ${response.message()}"
                    _resultAddFavorite.postValue(DataResult.Error(errorMessage))
                }
            } catch (e: IOException) {
                _resultAddFavorite.postValue(DataResult.Error("Network connection error!"))
            } catch (e: HttpException) {
                _resultAddFavorite.postValue(DataResult.Error("Error HTTP: ${e.message}"))
            } catch (e: Exception) {
                _resultAddFavorite.postValue(DataResult.Error("An unknown error has occurred!"))
            }
        }
    }


    private suspend fun getProductSame(category : Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repositoryProduct.searchByCategory(category)
                if (response.isSuccessful) {
                    val resultMessage = response.body()!!
                    _resultProductSame.postValue(DataResult.Success(resultMessage))
                } else {
                    val errorMessage = "Category product does not exist : ${response.message()}"
                    _resultProductSame.postValue(DataResult.Error(errorMessage))
                }
            } catch (e: IOException) {
                _resultProductSame.postValue(DataResult.Error("Network connection error!"))
            } catch (e: HttpException) {
                _resultProductSame.postValue(DataResult.Error("Error HTTP: ${e.message}"))
            } catch (e: Exception) {
                _resultProductSame.postValue(DataResult.Error("An unknown error has occurred!"))
            }
        }
    }

      fun getDetailProduct(id : Int) {
         viewModelScope.launch(Dispatchers.IO) {
             try {
                 val response = repositoryProduct.getDetailProduct(id)
                 if (response.isSuccessful) {
                     val result = response.body()!!
                     _resultDetail.postValue(DataResult.Success(result))
                     delay(100)
                     val categoryId = result.ProductType.id_category
                     getProductSame(categoryId)
                 } else {
                     val errorMessage = "Detail product does not exist : ${response.message()}"
                     _resultDetail.postValue(DataResult.Error(errorMessage))
                 }
             } catch (e: IOException) {
                 _resultDetail.postValue(DataResult.Error("Network connection error!"))
             } catch (e: HttpException) {
                 _resultDetail.postValue(DataResult.Error("Error HTTP: ${e.message}"))
             } catch (e: Exception) {
                 _resultDetail.postValue(DataResult.Error("An unknown error has occurred!"))
             }
         }
     }
}