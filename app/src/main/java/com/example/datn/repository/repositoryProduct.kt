package com.example.datn.repository

import com.example.datn.data.dataresult.ProductType
import com.example.datn.data.dataresult.ResultMessage
import com.example.datn.data.dataresult.ResultProductDetail
import com.example.datn.data.dataresult.Result_slideimages
import com.example.datn.data.dataresult.apiAddress.Addresse
import com.example.datn.data.dataresult.apiAddress.resultDefault
import com.example.datn.data.dataresult.apiAddress.resultHuyen
import com.example.datn.data.dataresult.apiAddress.resultListAddress
import com.example.datn.data.dataresult.apiAddress.resultTInh
import com.example.datn.data.dataresult.apiAddress.resultXa
import com.example.datn.data.dataresult.orders.ResultOrders
import com.example.datn.data.dataresult.resultCart
import com.example.datn.data.model.AddressRequest
import com.example.datn.data.model.addAddress
import com.example.datn.data.model.addCart
import com.example.datn.utils.network.RetrofitAddress
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

    suspend fun createAddOrders(addressRequest: AddressRequest) : Response<ResultMessage>{
        return RetrofitInstance.appApi.createAddOrders(addressRequest)
    }

    suspend fun getAllOrdersPending() : Response<ResultOrders>{
        return RetrofitInstance.appApi.getAllOrdersPending()
    }

    suspend fun getProvinces() : Response<resultTInh>{
        return RetrofitAddress.appApi.getProvinces()
    }

    suspend fun getDistricts(idTinh : String) : Response<resultHuyen>{
        return RetrofitAddress.appApi.getDistricts(idTinh)
    }

    suspend fun getWardsXa(districtId : String) : Response<resultXa>{
        return RetrofitAddress.appApi.getWards(districtId)
    }

    suspend fun addAddressUser(address: addAddress) : Response<ResultMessage>{
        return RetrofitInstance.appApi.addAddressUser(address)
    }

    suspend fun getAllAddress() : Response<resultListAddress>{
        return RetrofitInstance.appApi.getAllAddress()
    }

    suspend fun getDefaultAddress() : Response<resultDefault> {
        return RetrofitInstance.appApi.getDefaultAddress()
    }

    suspend fun getDetailAddress(idAddress : String?) :Response<resultDefault>{
        return RetrofitInstance.appApi.getDetailAddress(idAddress)
    }


    suspend fun getCartCount() : Response<ResultMessage>{
        return RetrofitInstance.appApi.getCartCount()
    }
    suspend fun getSearchProduct(name : String) : Response<ProductType>{
        return RetrofitInstance.appApi.getSearchProduct(name)
    }
}