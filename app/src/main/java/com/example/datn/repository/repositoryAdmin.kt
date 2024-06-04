package com.example.datn.repository

import com.example.datn.data.dataresult.ResultMessage
import com.example.datn.data.dataresult.ResultProductDetail
import com.example.datn.data.dataresult.orders.ResultOrders
import com.example.datn.data.dataresult.resultBarrChart
import com.example.datn.data.dataresult.resultCategory
import com.example.datn.data.dataresult.resultPieChart
import com.example.datn.data.dataresult.resultProductTYpe
import com.example.datn.data.dataresult.resultTotalDate
import com.example.datn.utils.network.RetrofitInstance
import com.velmurugan.paging3android.ProductResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Part

class repositoryAdmin {


    suspend fun getListProduct(page: Int): Response<ProductResponse> {
        return RetrofitInstance.adminApi.getProductsFavorite(page)
    }

    suspend fun getDetailProduct(id: Int): Response<ResultProductDetail> {
        return RetrofitInstance.appApi.getDetailProduct(id)
    }

    suspend fun addProductSize(
        size: RequestBody,
        id_type: RequestBody,
        stock: RequestBody,
        price: RequestBody,
        product_photo: MultipartBody.Part
    ): Response<ResultMessage> {
        return RetrofitInstance.adminApi.addProductSize(
            size,
            id_type,
            stock,
            price,
            product_photo
        )
    }

    suspend fun updateProductSize(
        id: RequestBody,
        size: RequestBody,
        stock: RequestBody,
        price: RequestBody,
        product_photo: MultipartBody.Part?
    ): Response<ResultMessage> {
        return RetrofitInstance.adminApi.updateProductSize(
            id,
            size,
            stock,
            price,
            product_photo
        )
    }

    suspend fun deleteProductSize(id: Int): Response<ResultMessage> {
        return RetrofitInstance.adminApi.deleteProductSize(id)
    }

    suspend fun getAllCategory(): Response<resultCategory> {
        return RetrofitInstance.adminApi.getAllCategory()
    }

    suspend fun addProductType(
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
    ): Response<ResultMessage> {
        return RetrofitInstance.adminApi.addProductType( id_category,
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
            image_product)
    }

    suspend fun getProductType(id : Int): Response<resultProductTYpe> {
        return RetrofitInstance.adminApi.getProductType(id)
    }

    suspend fun updateProductType(
      id: RequestBody,
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
    ): Response<ResultMessage> {
        return RetrofitInstance.adminApi.updateProductType( id,id_category,
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
            image_product)
    }

    suspend fun deleteType(id : Int) : Response<ResultMessage>{
        return  RetrofitInstance.adminApi.deleteType(id)
    }

    suspend fun getAllOrderConfirm() : Response<ResultOrders>{
        return RetrofitInstance.adminApi.getAllOrderConfirm()
    }

    suspend fun confirmOrder(idOrder : Int) : Response<ResultMessage>{
        return RetrofitInstance.adminApi.confirmOrder(idOrder)
    }

    suspend fun  getAllOrderPacking() : Response<ResultOrders> {
        return RetrofitInstance.adminApi.getAllOrderPacking()
    }

    suspend fun  getAllOrderShipping(): Response<ResultOrders> {
        return RetrofitInstance.adminApi.getAllOrderShipping()
    }

    suspend fun getAllOrderDelivery() :Response<ResultOrders> {
        return RetrofitInstance.adminApi.getAllOrderDelivery()
    }

    suspend fun getAllOrderReceived() : Response<ResultOrders>{
        return  RetrofitInstance.adminApi.getAllOrderReceived()
    }

    suspend fun getAllOrderCancel() : Response<ResultOrders>{
        return RetrofitInstance.adminApi.getAllOrderCancel()
    }

    suspend fun logout() : Response<ResultMessage>{
        return RetrofitInstance.adminApi.logout()
    }

    suspend fun getPieChart(): Response<resultPieChart>{
        return RetrofitInstance.adminApi.getPieChart()
    }

    suspend fun getBarChart(): Response<resultBarrChart> {
        return RetrofitInstance.adminApi.getBarChart()
    }

    suspend fun getAllTotal() : Response<resultTotalDate>{
        return RetrofitInstance.adminApi.getAllTotal()
    }
}