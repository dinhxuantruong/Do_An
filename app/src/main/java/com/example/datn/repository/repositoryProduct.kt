package com.example.datn.repository

import com.example.datn.data.dataresult.ProductType
import com.example.datn.data.dataresult.ResultMessage
import com.example.datn.data.dataresult.ResultProductDetail
import com.example.datn.data.dataresult.Result_slideimages
import com.example.datn.data.dataresult.orders.ResultOrders
import com.example.datn.data.dataresult.resultCart
import com.example.datn.data.model.addCart
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

    //add to card
    suspend fun addToCart(cart : addCart) : Response<ResultMessage>{
        return RetrofitInstance.appApi.addToCart(cart)
    }

    //get all cart
    suspend fun getAllCart() : Response<resultCart>{
        return RetrofitInstance.appApi.getAllCart()
    }

    //change cart
    suspend fun changeCart(cartId : Int) : Response<ResultMessage>{
        return RetrofitInstance.appApi.changeCart(cartId)
    }

    //check all box
    suspend fun checkAllBox() : Response<ResultMessage>{
        return RetrofitInstance.appApi.checkAllBox()
    }

    //delete check box all
    suspend fun deleteCheckBoxAll() : Response<ResultMessage>{
        return RetrofitInstance.appApi.deleteCheckBoxAll()
    }

    //delete cart
    suspend fun deleteCart() : Response<ResultMessage> {
        return RetrofitInstance.appApi.deleteCart()
    }

    //delete item cart
    suspend fun deleteItemCart(id : Int) : Response<ResultMessage>{
        return RetrofitInstance.appApi.deleteItemCart(id)
    }

    //plus item cart
    suspend fun plusItemCart(id : Int) : Response<ResultMessage>{
        return RetrofitInstance.appApi.plusItemCart(id)
    }

    //minus item cart
    suspend fun minusItemCart(id : Int) : Response<ResultMessage>{
        return RetrofitInstance.appApi.minusItemCart(id)
    }

    suspend fun checkStockStatus() : Response<ResultMessage>{
        return RetrofitInstance.appApi.checkStockStatus()
    }

    suspend fun checkoutOrders() : Response<resultCart>{
        return RetrofitInstance.appApi.checkoutOrders()
    }

    suspend fun createAddOrders() : Response<ResultMessage>{
        return RetrofitInstance.appApi.createAddOrders()
    }

    suspend fun getAllOrdersPending() : Response<ResultOrders>{
        return RetrofitInstance.appApi.getAllOrdersPending()
    }

}