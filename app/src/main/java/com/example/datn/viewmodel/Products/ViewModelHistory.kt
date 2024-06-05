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
import com.example.datn.data.dataresult.ResponseResult
import com.example.datn.data.dataresult.resultHistoryRating
import com.example.datn.repository.repositoryProduct
import com.example.datn.utils.Extension.ErrorBodyMessage.getErrorBodyMessage
import com.example.datn.utils.network.Constance
import com.velmurugan.paging3android.Adapter.CustomPagingSource
import com.velmurugan.paging3android.ProductType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class ViewModelHistory(private val repositoryProduct: repositoryProduct) : ViewModel() {

    private val _isLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _resultRating: MutableLiveData<ResponseResult<resultHistoryRating>> =
        MutableLiveData()
    val resultHistoryRating: LiveData<ResponseResult<resultHistoryRating>> get() = _resultRating


    val errorMessage = MutableLiveData<String>()


    fun getProductTypeMax(): LiveData<PagingData<ProductType>> {
        val page = Pager(
            config = PagingConfig(
                pageSize = Constance.NETWORK_PAGE_SIZE,
                enablePlaceholders = false,
                initialLoadSize = 2
            ),
            pagingSourceFactory = {
                CustomPagingSource { page ->
                    try {
                        val response = repositoryProduct.allProductsTypeMaxPage(page)
                        response.body()?.ProductTypes ?: emptyList()
                    } catch (e: Exception) {
                        throw IOException("Network connection error!", e)
                    }

                }
            }
        ).liveData.cachedIn(viewModelScope)

        return page
    }

    fun getProductTypeTime(): LiveData<PagingData<ProductType>> {
        val page = Pager(
            config = PagingConfig(
                pageSize = Constance.NETWORK_PAGE_SIZE,
                enablePlaceholders = false,
                initialLoadSize = 2
            ),
            pagingSourceFactory = {
                CustomPagingSource { page ->
                    try {
                        val response = repositoryProduct.allProductsTypeTimePage(page)
                        response.body()?.ProductTypes ?: emptyList()
                    } catch (e: Exception) {
                        throw IOException("Network connection error!", e)
                    }

                }
            }
        ).liveData.cachedIn(viewModelScope)

        return page
    }



    fun getViewHistory(): LiveData<PagingData<ProductType>> {
        val page = Pager(
            config = PagingConfig(
                pageSize = Constance.NETWORK_PAGE_SIZE,
                enablePlaceholders = false,
                initialLoadSize = 2
            ),
            pagingSourceFactory = {
                CustomPagingSource { page ->
                    try {
                        val response = repositoryProduct.getViewHistory(page)
                        response.body()?.ProductTypes ?: emptyList()
                    } catch (e: Exception) {
                        throw IOException("Network connection error!", e)
                    }

                }
            }
        ).liveData.cachedIn(viewModelScope)

        return page
    }


    fun getTypeCategory(id: Int): LiveData<PagingData<ProductType>> {
        val page = Pager(
            config = PagingConfig(
                pageSize = Constance.NETWORK_PAGE_SIZE,
                enablePlaceholders = false,
                initialLoadSize = 2
            ),
            pagingSourceFactory = {
                CustomPagingSource { page ->
                    try {
                        val response = repositoryProduct.getProductTypeCate(id, page)
                        response.body()?.ProductTypes ?: emptyList()
                    } catch (e: Exception) {
                        throw IOException("Network connection error!", e)
                    }

                }
            }
        ).liveData.cachedIn(viewModelScope)

        return page
    }

    fun getHistoryRating() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _isLoading.postValue(true)
                val response = repositoryProduct.getHistoryRating()
                if (response.isSuccessful) {
                    val resultMessage = response.body()!!
                    _resultRating.postValue(ResponseResult.Success(resultMessage))
                } else {
                    val errorBodyMessage = response.getErrorBodyMessage()
                    val finalErrorMessage =
                        if (errorBodyMessage != "Unknown error") errorBodyMessage else "Error"
                    _resultRating.postValue(ResponseResult.Error(finalErrorMessage))
                }
            } catch (e: IOException) {
                _resultRating.postValue(ResponseResult.Error("Network connection error!"))
            } catch (e: HttpException) {
                _resultRating.postValue(ResponseResult.Error("Error HTTP: ${e.message}"))
            } catch (e: Exception) {
                _resultRating.postValue(ResponseResult.Error("An unknown error has occurred!"))
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

}