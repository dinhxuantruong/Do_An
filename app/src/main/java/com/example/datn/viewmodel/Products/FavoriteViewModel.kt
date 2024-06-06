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
import com.example.datn.data.dataresult.ResultMessage
import com.example.datn.repository.repositoryProduct
import com.example.datn.data.dataresult.ResponseResult
import com.example.datn.utils.network.Constance
import com.example.datn.utils.network.Constance.Companion.NETWORK_PAGE_SIZE
import com.velmurugan.paging3android.Adapter.CustomPagingSource
import com.velmurugan.paging3android.ProductType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class FavoriteViewModel(private val repositoryProduct: repositoryProduct) : ViewModel() {
    val errorMessage = MutableLiveData<String>()

    fun getProductFavorite(): LiveData<PagingData<ProductType>> {
        val page = Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                enablePlaceholders = false,
                initialLoadSize = 2
            ),
            pagingSourceFactory = {
                CustomPagingSource { page ->
                    try {
                        val response = repositoryProduct.getProductsFavorite(page)
                        response.body()?.ProductTypes ?: emptyList()
                    } catch (e: Exception) {
                        throw IOException("Network connection error!", e)
                    }

                }
            }
        ).liveData.cachedIn(viewModelScope)

        return page
    }
    private val _resultAddFavorite : MutableLiveData<ResponseResult<ResultMessage>> = MutableLiveData()
    val resultAddFavorite : LiveData<ResponseResult<ResultMessage>> get() = _resultAddFavorite
    fun addFavorite(id : Int) {
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


}