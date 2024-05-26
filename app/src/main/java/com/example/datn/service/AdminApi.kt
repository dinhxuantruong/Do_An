package com.example.datn.service

import com.example.datn.data.dataresult.ResultMessage
import com.example.datn.data.dataresult.orders.ResultOrders
import com.example.datn.data.dataresult.resultCategory
import com.example.datn.data.dataresult.resultProductTYpe
import com.example.datn.data.model.BodyAddSize
import com.velmurugan.paging3android.ProductResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface AdminApi {

    @GET("product/page")
    suspend fun getProductsFavorite(
        @Query("page") page: Int,
    ): Response<ProductResponse>

    //add product size
    @Multipart
    @POST("product/addproduct")
    suspend fun addProductSize(
        @Part("size") size: RequestBody,
        @Part("id_type") id_type: RequestBody,
        @Part("stock") stock: RequestBody,
        @Part("price") price: RequestBody,
        @Part product_photo: MultipartBody.Part
    ): Response<ResultMessage>

    //update product
    @Multipart
    @POST("product/update/item")
    suspend fun updateProductSize(
        @Part("id") id: RequestBody,
        @Part("size") size: RequestBody,
        @Part("stock") stock: RequestBody,
        @Part("price") price: RequestBody,
        @Part product_photo: MultipartBody.Part?
    ): Response<ResultMessage>

    //delete product
    @POST("product/delete/item/{id}")
    suspend fun deleteProductSize(@Path("id") id: Int) : Response<ResultMessage>

    // all category
    @GET("product/category/all")
    suspend fun getAllCategory() : Response<resultCategory>

    //add product type
    @Multipart
    @POST("product/addtype")
    suspend fun addProductType(
        @Part("id_category") id_category: RequestBody,
        @Part("name") name: RequestBody,
        @Part("active_ingredients") active_ingredients: RequestBody,
        @Part("trademark") trademark: RequestBody,
        @Part("recipe") recipe: RequestBody,
        @Part("made") made: RequestBody,
        @Part("date") date: RequestBody,
        @Part("weight") weight: RequestBody,
        @Part("ingredient") ingredient: RequestBody,
        @Part("package_size") package_size: RequestBody,
        @Part("quantity") quantity: RequestBody,
        @Part("description") description: RequestBody,
        @Part image_product: MultipartBody.Part?
    ): Response<ResultMessage>

    @GET("product/one/producttype/{id}")
    suspend fun getProductType(@Path("id") id: Int) : Response<resultProductTYpe>

    //update product type
    @Multipart
    @POST("product/update/product/type")
    suspend fun updateProductType(
        @Part("id") id: RequestBody,
        @Part("id_category") id_category: RequestBody,
        @Part("name") name: RequestBody,
        @Part("active_ingredients") active_ingredients: RequestBody,
        @Part("trademark") trademark: RequestBody,
        @Part("recipe") recipe: RequestBody,
        @Part("made") made: RequestBody,
        @Part("date") date: RequestBody,
        @Part("weight") weight: RequestBody,
        @Part("ingredient") ingredient: RequestBody,
        @Part("package_size") package_size: RequestBody,
        @Part("quantity") quantity: RequestBody,
        @Part("description") description: RequestBody,
        @Part image_product: MultipartBody.Part?
    ): Response<ResultMessage>

    //delete product type
    @POST("product/delete/product/type/{id}")
    suspend fun deleteType(@Path("id") id : Int) : Response<ResultMessage>

    //đơn hàng chờ xác nhận
    @GET("admin/orders/pending")
    suspend fun getAllOrderConfirm(): Response<ResultOrders>

    //Xác nhận đơn hàng
    @POST("admin/change/order/status/{idOrder}")
    suspend fun confirmOrder(@Path("idOrder") idOrder : Int) : Response<ResultMessage>

    //Lấy danh sách đơn hàng cần đóng gói
    @GET("admin/orders/packing")
    suspend fun getAllOrderPacking() : Response<ResultOrders>

    //Lấy danh sách đơn hàng đang vận chuyển
    @GET("admin/orders/shipping")
    suspend fun getAllOrderShipping() : Response<ResultOrders>

    //Lấy danh sách đơn hàng đang giao hàng
    @GET("admin/orders/delivering")
    suspend fun getAllOrderDelivery() : Response<ResultOrders>

    //Danh sách đơn hàng đã hoàn thành
    @GET("admin/orders/received")
    suspend fun getAllOrderReceived() : Response<ResultOrders>

    //Danh sách đơn hàng đã hủy
    @GET("admin/orders/cancelled")
    suspend fun getAllOrderCancel() : Response<ResultOrders>
}