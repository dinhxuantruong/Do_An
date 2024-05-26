package com.example.datn.viewmodel.Admin

import android.util.Log
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
import com.example.datn.data.dataresult.ResultMessage
import com.example.datn.data.dataresult.ResultProductDetail
import com.example.datn.data.dataresult.resultCategory
import com.example.datn.data.dataresult.resultProductTYpe
import com.example.datn.repository.repositoryAdmin
import com.example.datn.utils.Extension.ErrorBodyMessage.getErrorBodyMessage
import com.example.datn.utils.network.Constance
import com.velmurugan.paging3android.Adapter.CustomPagingSource
import com.velmurugan.paging3android.ProductType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException
import java.io.IOException

class AdminViewModel(private val repositoryAdmin: repositoryAdmin) : ViewModel() {
    val errorMessage = MutableLiveData<String>()

    private val _resultDetail: MutableLiveData<ResponseResult<ResultProductDetail>> =
        MutableLiveData()
    val resultDetail: LiveData<ResponseResult<ResultProductDetail>> get() = _resultDetail

    private val _resultAddSize: MutableLiveData<ResponseResult<ResultMessage>> =
        MutableLiveData()
    val resultAddSize: LiveData<ResponseResult<ResultMessage>> get() = _resultAddSize

    private val _resultDeleteSize: MutableLiveData<ResponseResult<ResultMessage>> =
        MutableLiveData()
    val resultDeleteSize: LiveData<ResponseResult<ResultMessage>> get() = _resultDeleteSize

    private val _resultUpdateSize: MutableLiveData<ResponseResult<ResultMessage>> =
        MutableLiveData()
    val resultUpdateSize: LiveData<ResponseResult<ResultMessage>> get() = _resultUpdateSize

    private val _resultCate: MutableLiveData<ResponseResult<resultCategory>> =
        MutableLiveData()
    val resultCate: LiveData<ResponseResult<resultCategory>> get() = _resultCate

    private val _resultAddType: MutableLiveData<ResponseResult<ResultMessage>> =
        MutableLiveData()
    val resultAddType: LiveData<ResponseResult<ResultMessage>> get() = _resultAddType

    private val _resultUpdateType: MutableLiveData<ResponseResult<ResultMessage>> =
        MutableLiveData()
    val resultUpdateType: LiveData<ResponseResult<ResultMessage>> get() = _resultUpdateType

    private val _resultDeleteType: MutableLiveData<ResponseResult<ResultMessage>> =
        MutableLiveData()
    val resultDeleteType: LiveData<ResponseResult<ResultMessage>> get() = _resultDeleteType
    private val _resultType: MutableLiveData<ResponseResult<resultProductTYpe>> =
        MutableLiveData()
    val resultType: LiveData<ResponseResult<resultProductTYpe>> get() = _resultType
    private val _isLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading
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
                        val response = repositoryAdmin.getListProduct(page)
                        response.body()?.ProductTypes ?: emptyList()
                    } catch (e: Exception) {
                        throw IOException("Network connection error!", e)
                    }

                }
            }
        ).liveData.cachedIn(viewModelScope)

        return page
    }


    fun deleteProductType(id : Int){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _isLoading.postValue(true)
                val response = repositoryAdmin.deleteType(id)
                if (response.isSuccessful) {
                    val resultBody = response.body()!!
                    Log.e("MAIN", resultBody.toString())
                    _resultDeleteType.postValue(ResponseResult.Success(resultBody))
                } else {
                    val errorBodyMessage = response.getErrorBodyMessage()
                    val finalErrorMessage =
                        if (errorBodyMessage != "Unknown error") errorBodyMessage else "Error"
                    _resultDeleteType.postValue(ResponseResult.Error(finalErrorMessage))
                }
            } catch (e: IOException) {
                _resultDeleteType.postValue(ResponseResult.Error("Network connection error!"))
            } catch (e: HttpException) {
                _resultDeleteType.postValue(ResponseResult.Error("Error HTTP: ${e.message}"))
            } catch (e: Exception) {
                _resultDeleteType.postValue(ResponseResult.Error("An unknown error has occurred!"))
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun getProductType(id : Int){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _isLoading.postValue(true)
                val response = repositoryAdmin.getProductType(id)
                if (response.isSuccessful) {
                    val resultBody = response.body()!!
                    Log.e("MAIN", resultBody.toString())
                    _resultType.postValue(ResponseResult.Success(resultBody))
                } else {
                    val errorBodyMessage = response.getErrorBodyMessage()
                    val finalErrorMessage =
                        if (errorBodyMessage != "Unknown error") errorBodyMessage else "Error"
                    _resultType.postValue(ResponseResult.Error(finalErrorMessage))
                }
            } catch (e: IOException) {
                _resultType.postValue(ResponseResult.Error("Network connection error!"))
            } catch (e: HttpException) {
                _resultType.postValue(ResponseResult.Error("Error HTTP: ${e.message}"))
            } catch (e: Exception) {
                _resultType.postValue(ResponseResult.Error("An unknown error has occurred!"))
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun addProductType(
        id_category: RequestBody,
        name: RequestBody,
        active_ingredients: RequestBody,
        trademark: RequestBody,
        recipe: RequestBody,
        made: RequestBody,
        date: RequestBody,
        weight: RequestBody,
        ingredient: RequestBody,
        package_size: RequestBody,
        quantity: RequestBody,
        description: RequestBody,
        image_product: MultipartBody.Part?
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _isLoading.postValue(true)
                val response = repositoryAdmin.addProductType(
                    id_category,
                    name,
                    active_ingredients,
                    trademark,
                    recipe,
                    made,
                    date,
                    weight,
                    ingredient,
                    package_size,
                    quantity,
                    description,
                    image_product
                )
                if (response.isSuccessful) {
                    val resultBody = response.body()!!
                    Log.e("MAIN", resultBody.toString())
                    _resultAddType.postValue(ResponseResult.Success(resultBody))
                } else {
                    val errorBodyMessage = response.getErrorBodyMessage()
                    val finalErrorMessage =
                        if (errorBodyMessage != "Unknown error") errorBodyMessage else "Error"
                    _resultAddType.postValue(ResponseResult.Error(finalErrorMessage))
                }
            } catch (e: IOException) {
                _resultAddType.postValue(ResponseResult.Error("Network connection error!"))
            } catch (e: HttpException) {
                _resultAddType.postValue(ResponseResult.Error("Error HTTP: ${e.message}"))
            } catch (e: Exception) {
                _resultAddType.postValue(ResponseResult.Error("An unknown error has occurred!"))
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun updateProductType(
        id : RequestBody,
        id_category: RequestBody,
        name: RequestBody,
        active_ingredients: RequestBody,
        trademark: RequestBody,
        recipe: RequestBody,
        made: RequestBody,
        date: RequestBody,
        weight: RequestBody,
        ingredient: RequestBody,
        package_size: RequestBody,
        quantity: RequestBody,
        description: RequestBody,
        image_product: MultipartBody.Part?
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _isLoading.postValue(true)
                val response = repositoryAdmin.updateProductType(
                    id,
                    id_category,
                    name,
                    active_ingredients,
                    trademark,
                    recipe,
                    made,
                    date,
                    weight,
                    ingredient,
                    package_size,
                    quantity,
                    description,
                    image_product
                )
                if (response.isSuccessful) {
                    val resultBody = response.body()!!
                    Log.e("MAIN", resultBody.toString())
                    _resultUpdateType.postValue(ResponseResult.Success(resultBody))
                } else {
                    val errorBodyMessage = response.getErrorBodyMessage()
                    val finalErrorMessage =
                        if (errorBodyMessage != "Unknown error") errorBodyMessage else "Error"
                    _resultUpdateType.postValue(ResponseResult.Error(finalErrorMessage))
                }
            } catch (e: IOException) {
                _resultUpdateType.postValue(ResponseResult.Error("Network connection error!"))
            } catch (e: HttpException) {
                _resultUpdateType.postValue(ResponseResult.Error("Error HTTP: ${e.message}"))
            } catch (e: Exception) {
                _resultUpdateType.postValue(ResponseResult.Error("An unknown error has occurred!"))
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun getAllCategory() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _isLoading.postValue(true)
                val response = repositoryAdmin.getAllCategory()
                if (response.isSuccessful) {
                    val resultBody = response.body()!!
                    Log.e("MAIN", resultBody.toString())
                    _resultCate.postValue(ResponseResult.Success(resultBody))
                } else {
                    val errorBodyMessage = response.getErrorBodyMessage()
                    val finalErrorMessage =
                        if (errorBodyMessage != "Unknown error") errorBodyMessage else "Error"
                    _resultCate.postValue(ResponseResult.Error(finalErrorMessage))
                }
            } catch (e: IOException) {
                _resultCate.postValue(ResponseResult.Error("Network connection error!"))
            } catch (e: HttpException) {
                _resultCate.postValue(ResponseResult.Error("Error HTTP: ${e.message}"))
            } catch (e: Exception) {
                _resultCate.postValue(ResponseResult.Error("An unknown error has occurred!"))
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun deProductSize(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _isLoading.postValue(true)
                val response = repositoryAdmin.deleteProductSize(id)
                if (response.isSuccessful) {
                    val resultBody = response.body()!!
                    Log.e("MAIN", resultBody.toString())
                    _resultDeleteSize.postValue(ResponseResult.Success(resultBody))
                } else {
                    val errorBodyMessage = response.getErrorBodyMessage()
                    val finalErrorMessage =
                        if (errorBodyMessage != "Unknown error") errorBodyMessage else "Error"
                    _resultDeleteSize.postValue(ResponseResult.Error(finalErrorMessage))
                }
            } catch (e: IOException) {
                _resultDeleteSize.postValue(ResponseResult.Error("Network connection error!"))
            } catch (e: HttpException) {
                _resultDeleteSize.postValue(ResponseResult.Error("Error HTTP: ${e.message}"))
            } catch (e: Exception) {
                _resultDeleteSize.postValue(ResponseResult.Error("An unknown error has occurred!"))
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun upProductSize(
        id: RequestBody, size: RequestBody,
        stock: RequestBody,
        price: RequestBody,
        product_photo: MultipartBody.Part?
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _isLoading.postValue(true)
                val response = repositoryAdmin.updateProductSize(
                    id,
                    size,
                    stock,
                    price,
                    product_photo
                )
                if (response.isSuccessful) {
                    val resultBody = response.body()!!
                    Log.e("MAIN", resultBody.toString())
                    _resultUpdateSize.postValue(ResponseResult.Success(resultBody))
                } else {
                    val errorBodyMessage = response.getErrorBodyMessage()
                    val finalErrorMessage =
                        if (errorBodyMessage != "Unknown error") errorBodyMessage else "Error"
                    _resultUpdateSize.postValue(ResponseResult.Error(finalErrorMessage))
                }
            } catch (e: IOException) {
                _resultUpdateSize.postValue(ResponseResult.Error("Network connection error!"))
            } catch (e: HttpException) {
                _resultUpdateSize.postValue(ResponseResult.Error("Error HTTP: ${e.message}"))
            } catch (e: Exception) {
                _resultUpdateSize.postValue(ResponseResult.Error("An unknown error has occurred!"))
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun addProductSize(
        size: RequestBody,
        id_type: RequestBody,
        stock: RequestBody,
        price: RequestBody,
        product_photo: MultipartBody.Part
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _isLoading.postValue(true)
                val response = repositoryAdmin.addProductSize(
                    size,
                    id_type,
                    stock,
                    price,
                    product_photo
                )
                if (response.isSuccessful) {
                    val resultBody = response.body()!!
                    Log.e("MAIN", resultBody.toString())
                    _resultAddSize.postValue(ResponseResult.Success(resultBody))
                } else {
                    val errorBodyMessage = response.getErrorBodyMessage()
                    val finalErrorMessage =
                        if (errorBodyMessage != "Unknown error") errorBodyMessage else "Error"
                    _resultAddSize.postValue(ResponseResult.Error(finalErrorMessage))
                }
            } catch (e: IOException) {
                _resultAddSize.postValue(ResponseResult.Error("Network connection error!"))
            } catch (e: HttpException) {
                _resultAddSize.postValue(ResponseResult.Error("Error HTTP: ${e.message}"))
            } catch (e: Exception) {
                _resultAddSize.postValue(ResponseResult.Error("An unknown error has occurred!"))
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun getDetailProduct(idAddress: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _isLoading.postValue(true)
                val response = repositoryAdmin.getDetailProduct(idAddress)
                if (response.isSuccessful) {
                    val resultBody = response.body()!!
                    Log.e("MAIN", resultBody.toString())
                    _resultDetail.postValue(ResponseResult.Success(resultBody))
                } else {
                    val errorBodyMessage = response.getErrorBodyMessage()
                    val finalErrorMessage =
                        if (errorBodyMessage != "Unknown error") errorBodyMessage else "Error"
                    _resultDetail.postValue(ResponseResult.Error(finalErrorMessage))
                }
            } catch (e: IOException) {
                _resultDetail.postValue(ResponseResult.Error("Network connection error!"))
            } catch (e: HttpException) {
                _resultDetail.postValue(ResponseResult.Error("Error HTTP: ${e.message}"))
            } catch (e: Exception) {
                _resultDetail.postValue(ResponseResult.Error("An unknown error has occurred!"))
            } finally {
                _isLoading.postValue(false)
            }
        }
    }
}