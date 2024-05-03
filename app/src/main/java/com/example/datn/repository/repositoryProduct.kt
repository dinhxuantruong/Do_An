package com.example.datn.repository

import com.example.datn.data.ProductType
import com.example.datn.data.ResultMessage
import com.example.datn.data.ResultProductDetail
import com.example.datn.data.Result_slideimages
import com.example.datn.utils.network.RetrofitInstance
import com.velmurugan.paging3android.ProductResponse
import retrofit2.Response

class repositoryProduct() {


    suspend fun getImageSlide() : Response<Result_slideimages>{
        return RetrofitInstance.appApi.getImageSlide()
    }

    suspend fun getImageSlideOut() : Response<Result_slideimages>{
        return RetrofitInstance.appApi.getImageSlideOut()
    }

    suspend fun authLogout(): Response<ResultMessage> {
        return RetrofitInstance.appApi.authLogout()
    }

    suspend fun allProductsTypeMax() : Response<ProductType>{
        return RetrofitInstance.appApi.allProductsTypeMax()
    }
    suspend fun allProductsType() : Response<ProductType>{
        return RetrofitInstance.appApi.allProductsTypeLimit()
    }

    suspend fun getALlProductType() : Response<ProductType>{
        return RetrofitInstance.appApi.allProductsType()
    }

      suspend fun allProductsTypeTime() : Response<ProductType>{
        return RetrofitInstance.appApi.allProductsTypeTime()
    }

    //get detail product
    suspend fun getDetailProduct(id : Int) : Response<ResultProductDetail>{
        return  RetrofitInstance.appApi.getDetailProduct(id)
    }

    //add favorite
    suspend fun  addFavorite(id : Int) : Response<ResultMessage>{
        return RetrofitInstance.appApi.addFavorite(id)
    }

    //get product_type same
    suspend fun searchByCategory(category : Int) : Response<ProductType>{
        return RetrofitInstance.appApi.searchByCategory(category)
    }

    //get page product
    suspend fun getProductsPage(page : Int) : Response<ProductResponse>{
        return RetrofitInstance.appApi.getProductsPage(page)
    }

    //get all favorite
    suspend fun getProductsFavorite(page: Int) : Response<ProductResponse>{
        return RetrofitInstance.appApi.getProductsFavorite(page)
    }
}