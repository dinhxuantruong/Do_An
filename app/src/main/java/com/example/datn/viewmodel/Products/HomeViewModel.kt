package com.example.datn.viewmodel.Products

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.liveData
import com.example.datn.data.ProductType
import com.example.datn.data.ProductTypeX
import com.example.datn.data.Result_slideimages
import com.example.datn.repository.repositoryProduct
import com.example.datn.utils.DataResult
import com.example.datn.utils.network.Constance.Companion.NETWORK_PAGE_SIZE
import com.velmurugan.paging3android.Adapter.CustomPagingSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class HomeViewModel(private val repositoryProduct: repositoryProduct) : ViewModel() {

    private val _resultImageSlide: MutableLiveData<DataResult<Result_slideimages>> =
        MutableLiveData()
    val resultImage: LiveData<DataResult<Result_slideimages>> get() = _resultImageSlide

    private val _resultImageSlideOut: MutableLiveData<DataResult<Result_slideimages>> =
        MutableLiveData()
    val resultImageOut: LiveData<DataResult<Result_slideimages>> get() = _resultImageSlideOut

    private val _resultAllPtypeMax: MutableLiveData<DataResult<ProductType>> = MutableLiveData()
    val resultAllPtypeMax: LiveData<DataResult<ProductType>> get() = _resultAllPtypeMax

    private val _resultAllprTime: MutableLiveData<DataResult<ProductType>> = MutableLiveData()
    val resultAllPrTime: LiveData<DataResult<ProductType>> get() = _resultAllprTime

    val errorMessage = MutableLiveData<String>()

    private val _resultProductAll : MutableLiveData<DataResult<ProductType>> = MutableLiveData()
    val resultProductTypeAll : LiveData<DataResult<ProductType>> get() =  _resultProductAll

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
                    _resultImageSlide.postValue(DataResult.Success(slideResponse.body()!!))
                    delay(1000)
                    val productsResponse = productsDeferred.await()
                    if (productsResponse.isSuccessful) {
                        _resultAllPtypeMax.postValue(DataResult.Success(productsResponse.body()!!))
                        getAllProductTime()
                        delay(100)
                        getAlImageOut()
                        delay(120)
                        getAlIProductType()
                    } else {
                        _resultAllPtypeMax.postValue(DataResult.Error("allProductsType unsuccessful: ${productsResponse.message()}"))
                    }
                } else {
                    _resultImageSlide.postValue(DataResult.Error("getImageSlide unsuccessful: ${slideResponse.message()}"))
                }
            } catch (e: Exception) {
                handleException(e)
            }
        }
    }




    private suspend fun getAllProductTime() {
        try {
            val response = repositoryProduct.allProductsTypeTime()
            if (response.isSuccessful) {
                _resultAllprTime.postValue(DataResult.Success(response.body()!!))
            } else {
                _resultAllprTime.postValue(DataResult.Error("Account does not exist : ${response.message()}"))
            }
        } catch (e: Exception) {
            handleException(e)
        }
    }

    private suspend fun getAlImageOut() {
        try {
            val response = repositoryProduct.getImageSlideOut()
            if (response.isSuccessful) {
                _resultImageSlideOut.postValue(DataResult.Success(response.body()!!))
            } else {
                _resultImageSlideOut.postValue(DataResult.Error("Image out does not exist : ${response.message()}"))
            }
        } catch (e: Exception) {
            handleException(e)
        }
    }

    private suspend fun getAlIProductType() {
        try {
            val response = repositoryProduct.allProductsType()
            if (response.isSuccessful) {
                _resultProductAll.postValue(DataResult.Success(response.body()!!))
            } else {
                _resultProductAll.postValue(DataResult.Error("Product out does not exist : ${response.message()}"))
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
        _resultImageSlide.postValue(DataResult.Error(errorMessage))
        _resultAllPtypeMax.postValue(DataResult.Error(errorMessage))
        _resultAllprTime.postValue(DataResult.Error(errorMessage))
    }

    fun cancelJobs() {
        viewModelScope.coroutineContext.cancelChildren()
    }
}
