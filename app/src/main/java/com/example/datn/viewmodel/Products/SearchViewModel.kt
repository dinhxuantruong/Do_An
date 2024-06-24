package com.example.datn.viewmodel.Products

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.datn.data.dataresult.ProductType
import com.example.datn.data.dataresult.ResponseResult
import com.example.datn.data.dataresult.resultCart
import com.example.datn.repository.repositoryProduct
import com.example.datn.utils.Extension.ErrorBodyMessage.getErrorBodyMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class SearchViewModel(private val repositoryProduct: repositoryProduct) : ViewModel() {
    private val _isLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _sort = MutableLiveData<String>()
    val sort: LiveData<String> get() = _sort
    fun setSort(sortValue: String) {
        _sort.value = sortValue
    }

    private val _resultSearchProduct : MutableLiveData<ResponseResult<ProductType>> = MutableLiveData()
    val resultSearchProduct : LiveData<ResponseResult<ProductType>> get() =  _resultSearchProduct

    fun getAllProductTypeSearch(name : String, startPrice : Int?,
                                endPrice : Int?,
                                sort : String?) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _isLoading.postValue(true) // Show ProgressBar
                val response = repositoryProduct.getSearchProduct(name,startPrice,endPrice,sort)
                if (response.isSuccessful) {
                    val logout = response.body()!!
                    _resultSearchProduct.postValue(ResponseResult.Success(logout))
                } else {
                    val errorBodyMessage = response.getErrorBodyMessage()
                    val finalErrorMessage = if (errorBodyMessage != "Unknown error") errorBodyMessage else "Error!"
                    _resultSearchProduct.postValue(ResponseResult.Error(finalErrorMessage))
                }
            } catch (e: IOException) {
                _resultSearchProduct.postValue(ResponseResult.Error("Network connection error2!"))
            } catch (e: HttpException) {
                _resultSearchProduct.postValue(ResponseResult.Error("Error HTTP: ${e.message}"))
            } catch (e: Exception) {
                _resultSearchProduct.postValue(ResponseResult.Error("An unknown error has occurred!"))
            }finally {
                _isLoading.postValue(false)
            }
        }
    }

}