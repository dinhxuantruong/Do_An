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
import com.example.datn.repository.repositoryProduct
import com.example.datn.utils.network.Constance
import com.velmurugan.paging3android.Adapter.CustomPagingSource
import com.velmurugan.paging3android.ProductType
import java.io.IOException

class FavoriteViewModel(private val repositoryProduct: repositoryProduct) : ViewModel() {
    val errorMessage = MutableLiveData<String>()

    fun getProductFavorite(): LiveData<PagingData<ProductType>> {
        val page = Pager(
            config = PagingConfig(
                pageSize = Constance.NETWORK_PAGE_SIZE,
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
}