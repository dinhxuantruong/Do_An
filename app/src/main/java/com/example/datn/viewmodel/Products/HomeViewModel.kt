package com.example.datn.viewmodel.Products

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.liveData
import com.example.datn.data.dataresult.ProductType
import com.example.datn.data.dataresult.Result_slideimages
import com.example.datn.repository.repositoryProduct
import com.example.datn.data.dataresult.ResponseResult
import com.example.datn.data.dataresult.ResultMessage
import com.example.datn.utils.network.Constance.Companion.NETWORK_PAGE_SIZE
import com.velmurugan.paging3android.Adapter.CustomPagingSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class HomeViewModel( private val repositoryProduct: repositoryProduct) : ViewModel() {

    private val _resultImageSlide: MutableLiveData<ResponseResult<Result_slideimages>> =
        MutableLiveData()
    val resultImage: LiveData<ResponseResult<Result_slideimages>> get() = _resultImageSlide

    private val _resultImageSlideOut: MutableLiveData<ResponseResult<Result_slideimages>> =
        MutableLiveData()
    val resultImageOut: LiveData<ResponseResult<Result_slideimages>> get() = _resultImageSlideOut

    private val _resultAllPtypeMax: MutableLiveData<ResponseResult<ProductType>> = MutableLiveData()
    val resultAllPtypeMax: LiveData<ResponseResult<ProductType>> get() = _resultAllPtypeMax

    private val _resultAllprTime: MutableLiveData<ResponseResult<ProductType>> = MutableLiveData()
    val resultAllPrTime: LiveData<ResponseResult<ProductType>> get() = _resultAllprTime

    val errorMessage = MutableLiveData<String>()

    private val _resultGetAllProType : MutableLiveData<ResponseResult<ProductType>>  = MutableLiveData()
    val resultGetAllPrType : LiveData<ResponseResult<ProductType>> get() = _resultGetAllProType

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
    fun getAllProductType() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _isLoading.postValue(true) // Show ProgressBar
                val response = repositoryProduct.getALlProductType()
                if (response.isSuccessful) {
                    val logout = response.body()!!
                    _resultGetAllProType.postValue(ResponseResult.Success(logout))
                } else {
                    val errorMessage = "Logout unsuccessful: ${response.message()}"
                    _resultGetAllProType.postValue(ResponseResult.Error(errorMessage))
                }
            } catch (e: IOException) {
                _resultGetAllProType.postValue(ResponseResult.Error("Network connection error2!"))
            } catch (e: HttpException) {
                _resultGetAllProType.postValue(ResponseResult.Error("Error HTTP: ${e.message}"))
            } catch (e: Exception) {
                _resultGetAllProType.postValue(ResponseResult.Error("An unknown error has occurred!"))
            }finally {
                _isLoading.postValue(false)
            }
        }
    }


    private val _resultProductAll : MutableLiveData<ResponseResult<ProductType>> = MutableLiveData()
    val resultProductTypeAll : LiveData<ResponseResult<ProductType>> get() =  _resultProductAll

    fun getMovieList2(): LiveData<PagingData<com.velmurugan.paging3android.ProductType>> {
        val page = Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                enablePlaceholders = false,
                initialLoadSize = 2
            ),
            pagingSourceFactory = {
                CustomPagingSource { page ->
                    try {
                        val response = repositoryProduct.getProductsPage(page)
                        response.body()?.ProductTypes ?: emptyList()
                    } catch (e: Exception) {
                        throw IOException("Network connection error!", e)
                    }

                }
            }
        ).liveData.cachedIn(viewModelScope)

        return page
    }
    fun getImageSlideAndAllProductsType() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val slideDeferred = async { repositoryProduct.getImageSlide() }
                val productsDeferred = async { repositoryProduct.allProductsTypeMax() }

                val slideResponse = slideDeferred.await()
                if (slideResponse.isSuccessful) {
                    _resultImageSlide.postValue(ResponseResult.Success(slideResponse.body()!!))
                } else {
                    _resultImageSlide.postValue(ResponseResult.Error("getImageSlide unsuccessful: ${slideResponse.message()}"))
                }

                val productsResponse = productsDeferred.await()
                if (productsResponse.isSuccessful) {
                    _resultAllPtypeMax.postValue(ResponseResult.Success(productsResponse.body()!!))
                } else {
                    _resultAllPtypeMax.postValue(ResponseResult.Error("allProductsType unsuccessful: ${productsResponse.message()}"))
                }

                // Gọi các hàm tiếp theo mà không quan tâm đến kết quả trả về
                getAllProductTime()
                getAlImageOut()
                getAlIProductType()
            } catch (e: Exception) {
                handleException(e)
            }
        }
    }



    private suspend fun getAllProductTime() {
        try {
            val response = repositoryProduct.allProductsTypeTime()
            if (response.isSuccessful) {
                _resultAllprTime.postValue(ResponseResult.Success(response.body()!!))
            } else {
                _resultAllprTime.postValue(ResponseResult.Error("Account does not exist : ${response.message()}"))
            }
        } catch (e: Exception) {
            handleException(e)
        }
    }

    private suspend fun getAlImageOut() {
        try {
            val response = repositoryProduct.getImageSlideOut()
            if (response.isSuccessful) {
                _resultImageSlideOut.postValue(ResponseResult.Success(response.body()!!))
            } else {
                _resultImageSlideOut.postValue(ResponseResult.Error("Image out does not exist : ${response.message()}"))
            }
        } catch (e: Exception) {
            handleException(e)
        }
    }

    private suspend fun getAlIProductType() {
        try {
            val response = repositoryProduct.allProductsType()
            if (response.isSuccessful) {
                _resultProductAll.postValue(ResponseResult.Success(response.body()!!))
            } else {
                _resultProductAll.postValue(ResponseResult.Error("Product out does not exist : ${response.message()}"))
            }
        } catch (e: Exception) {
            handleException(e)
        }
    }

    private fun handleException(e: Exception) {
        val errorMessage = when (e) {
            is IOException -> "Network connection error!"
            is HttpException -> "Error HTTP: ${e.message()}"
            else -> "An unknown error has occurred!"
        }
        _resultImageSlide.postValue(ResponseResult.Error(errorMessage))
        _resultAllPtypeMax.postValue(ResponseResult.Error(errorMessage))
        _resultAllprTime.postValue(ResponseResult.Error(errorMessage))
    }

    fun cancelJobs() {
        viewModelScope.coroutineContext.cancelChildren()
    }
}
