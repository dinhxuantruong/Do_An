package com.example.datn.service

import com.example.datn.data.dataresult.ProductType
import com.example.datn.data.dataresult.ResultMessage
import com.example.datn.data.dataresult.ResultProductDetail
import com.example.datn.data.dataresult.Result_slideimages
import com.example.datn.data.dataresult.Resutl_RefreshToken
import com.example.datn.data.dataresult.User
import com.example.datn.data.dataresult.UserX
import com.example.datn.data.dataresult.apiAddress.resultDefault
import com.example.datn.data.dataresult.apiAddress.resultHuyen
import com.example.datn.data.dataresult.apiAddress.resultListAddress
import com.example.datn.data.dataresult.apiAddress.resultTInh
import com.example.datn.data.dataresult.apiAddress.resultXa
import com.example.datn.data.dataresult.orders.ResultOrders
import com.example.datn.data.dataresult.orderstatistics
import com.example.datn.data.dataresult.resultCart
import com.example.datn.data.dataresult.resultHistoryRating
import com.example.datn.data.dataresult.resultOrderDetails
import com.example.datn.data.model.AcceptOTP
import com.example.datn.data.model.AddressRequest
import com.example.datn.data.model.Auth
import com.example.datn.data.model.ForgetPass
import com.example.datn.data.model.Register
import com.example.datn.data.model.addAddress
import com.example.datn.data.model.addCart
import com.example.datn.data.model.google_input
import com.example.datn.data.model.loginWithGoogle
import com.example.datn.data.model.ratingBody
import com.example.datn.data.model.sendOTP
import com.velmurugan.paging3android.ProductResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface myApi {

    //Login
    @POST("auth/login")
    suspend fun authLogin(@Body auth: Auth): Response<User>

    //Get refresh token
    @POST("token")
    suspend fun getRefreshToken(@Body google: google_input): Response<Resutl_RefreshToken>

    //Login with google
    @POST("auth/google")
    suspend fun loginWithGoogle(@Body lg: loginWithGoogle): Response<User>

    //Register
    @POST("auth/register")
    suspend fun authRegister1(@Body register: Register): Response<ResultMessage>

    @POST("auth/register2")
    suspend fun authAcceptRegister(@Body acceptOTP: AcceptOTP): Response<ResultMessage>

    //Forgot password
    @POST("auth/forgot")
    suspend fun authForgetPassword(@Body forgetPass: ForgetPass): Response<ResultMessage>

    //Send otp
    @POST("sendotp")
    suspend fun authSendOTP(@Body sendOTP: sendOTP): Response<ResultMessage>

    //Auth check account
    @POST("auth/check/account")
    suspend fun authCheckAccountForget(@Body sendOTP: sendOTP): Response<ResultMessage>

    //Logout
    @POST("auth/logout")
    suspend fun authLogout(): Response<ResultMessage>


    //Slide images
    @GET("get/slide/images")
    suspend fun getImageSlide(): Response<Result_slideimages>

    //all product max
    @GET("product/all/product/type/max")
    suspend fun allProductsTypeMax(): Response<ProductType>
    @GET("product/all/product/type/max/page")
    suspend fun allProductsTypeMax2(@Query("page") page: Int): Response<ProductResponse>

    //all productype limit
    @GET("product/all/product/type/limit")
    suspend fun allProductsTypeLimit(): Response<ProductType>

    //get all product
    @GET("product/all/product/type")
    suspend fun allProductsType(): Response<ProductType>


    //Sản phẩm mới
    @GET("product/all/product/type/time")
    suspend fun allProductsTypeTime(): Response<ProductType>

    @GET("product/all/product/type/time/page")
    suspend fun allProductsTypeTimePage(@Query("page") page: Int): Response<ProductResponse>

    //get all image out
    @GET("get/slide/images/out")
    suspend fun getImageSlideOut(): Response<Result_slideimages>

    //get product details
    @GET("product/get/detail/{id}")
    suspend fun getDetailProduct(@Path("id") id: Int): Response<ResultProductDetail>

    //add favorite
    @POST("product/like/{id}")
    suspend fun addFavorite(@Path("id") id: Int): Response<ResultMessage>

    //get product_type same
    @GET("product/search/category")
    suspend fun searchByCategory(@Query("category_id") categoryId: Int): Response<ProductType>

    //get paging product
    @GET("product/page")
    suspend fun getProductsPage(
        @Query("page") page: Int,
    ): Response<ProductResponse>


    @GET("product/page/desc")
    suspend fun getProductsPageDesc(
        @Query("page") page: Int,
    ): Response<ProductResponse>

    @GET("product/page/asc")
    suspend fun getProductsPageAsc(
        @Query("page") page: Int,
    ): Response<ProductResponse>



    //get all product favorite
    @GET("product/details/favorite/like")
    suspend fun getProductsFavorite(
        @Query("page") page: Int,
    ): Response<ProductResponse>

    //add cart
    @POST("product/add/cart/item")
    suspend fun addToCart(@Body cart : addCart) : Response<ResultMessage>

    //get all cart
    @GET("product/get/cart/item")
    suspend fun getAllCart() : Response<resultCart>

    //change cart
    @POST("product/change/status/item/cart/{idCart}")
    suspend fun changeCart(@Path("idCart") idCart : Int) : Response<ResultMessage>

    //check all box
    @POST("product/all/check")
    suspend fun checkAllBox() : Response<ResultMessage>

    //delete check box all
    @POST("product/delete/check/box")
    suspend fun deleteCheckBoxAll() : Response<ResultMessage>

    //delete cart
    @POST("product/delete/cart")
    suspend fun deleteCart() : Response<ResultMessage>

    //delete item cart

    @POST("product/delete/item/cart/{id}")
    suspend fun deleteItemCart(@Path("id") id : Int) : Response<ResultMessage>

    //plus item cart
    @POST("product/plus/item/cart/{id}")
    suspend fun plusItemCart(@Path("id") id : Int) : Response<ResultMessage>


    //minus item cart
    @POST("product/minus/item/cart/{id}")
    suspend fun minusItemCart(@Path("id") id : Int) : Response<ResultMessage>

    //check stock status
    @GET("product/cart/checkout")
    suspend fun checkStockStatus() : Response<ResultMessage>

    //checkout
    @GET("cart/item/orders/status")
    suspend fun  checkoutOrders() : Response<resultCart>

    //create order
    @POST("product/order/by/user")
    suspend fun createAddOrders(@Body addressRequest: AddressRequest): Response<ResultMessage>

    //get all orders
    @GET("product/pending/orders")
    suspend fun getAllOrdersPending() : Response<ResultOrders>

    // api tỉnh thành
    @GET("api-tinhthanh/1/0.htm")
    suspend fun getProvinces(): Response<resultTInh>

    @GET("api-tinhthanh/2/{provinceId}.htm")
    suspend fun getDistricts(@Path("provinceId") provinceId: String): Response<resultHuyen>

    @GET("api-tinhthanh/3/{districtId}.htm")
    suspend fun getWards(@Path("districtId") districtId: String): Response<resultXa>

    //add address
    @POST("product/add/user/addresses")
    suspend fun addAddressUser(@Body address : addAddress) : Response<ResultMessage>

    //all địa chi
    @GET("product/get/all/address")
    suspend fun getAllAddress() : Response<resultListAddress>

    //lấy địa chỉ mặc định
    @GET("product/get/default/address")
    suspend fun getDefaultAddress() : Response<resultDefault>

    //Lấy địa chỉ theo id
    @GET("product/get/detail/address/user/{idaddess}")
    suspend fun getDetailAddress(@Path("idaddess") idaddess : String?) : Response<resultDefault>

    //get cart count
    @GET("product/get/cart/count/user")
    suspend fun getCartCount() : Response<ResultMessage>

    //tìm kiếm sản phẩm
    @GET("user/search/type/all")
    suspend fun getSearchProduct(@Query("name") name : String) : Response<ProductType>

    //Hủy đơn hàng
    @POST("user/canncel/order/{orderId}")
    suspend fun deleteOrder(@Path("orderId") orderId : Int) : Response<ResultMessage>

    //danh sách đơn hàng người dùng đang đóng gói
    @GET("user/packing/orders")
    suspend fun getAllOrderPacking() : Response<ResultOrders>

    //Danh sách đơn hàng đang vận chuyển
    @GET("user/shipping/orders")
    suspend fun getAllOrderShipping() : Response<ResultOrders>


    //Danh sách đơn hàng đang giao
    @GET("user/delivering/orders")
    suspend fun getAllOrderDelivering() : Response<ResultOrders>


    //Danh sách đơn hàng đã nhận
    @GET("user/received/orders")
    suspend fun getAllOrderReceived() : Response<ResultOrders>


    //Danh sách đơn hàng đã hủy
    @GET(" user/cancelled/orders")
    suspend fun getAllOrderCancelled(): Response<ResultOrders>

    //Chi tiết đơn hàng
    @GET("user/details/orders/{orderId}")
    suspend fun getDetailsOrder(@Path("orderId") orderId: Int): Response<resultOrderDetails>


    //Logout
    @POST("auth/logout")
    suspend fun logout(): Response<ResultMessage>


    //Lấy thông tin người dùng
    @GET("profile/user/details")
    suspend fun getProfileUser(): Response<UserX>


    @Multipart
    @POST("auth/user/profile/new")
    suspend fun updateProFileUser(
        @Part("name") name: RequestBody,
        @Part("profession") profession: RequestBody,
        @Part("phone") phone: RequestBody,
        @Part profile_photo: MultipartBody.Part?
    ): Response<ResultMessage>

    //order-statistics
    @GET("order-statistics")
    suspend fun getOrderStatistics() : Response<orderstatistics>

    //Đánh giá sản phẩm
    @POST("user/rate/order/{id}")
    suspend fun getRatingOrder(@Path("id") id : Int,
                               @Body ratingBody : ratingBody
    ) : Response<ResultMessage>

    //Danh sách lịch sử xem
    @GET("product/history/view/type")
    suspend fun getViewHistory(
        @Query("page") page: Int,
    ): Response<ProductResponse>

    //Lấy danh sachsc type theo mã category
    @GET("product/all/by/category/{categoryId}")
    suspend fun getProductTypeCate(@Path("categoryId") id : Int,
                                   @Query("page") page: Int) : Response<ProductResponse>

    //lấy danh sách lịch sử đáng giá
    @GET("get/all/reviewed/products")
    suspend fun getHistoryRating() : Response<resultHistoryRating>
}
