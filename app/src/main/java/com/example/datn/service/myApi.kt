package com.example.datn.service

import com.example.datn.data.ResultMessage
import com.example.datn.data.Resutl_RefreshToken
import com.example.datn.data.User
import com.example.datn.data.model.AcceptOTP
import com.example.datn.data.model.Auth
import com.example.datn.data.model.ForgetPass
import com.example.datn.data.model.Register
import com.example.datn.data.model.google_input
import com.example.datn.data.model.loginWithGoogle
import com.example.datn.data.model.sendOTP
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface myApi {

    //Login
    @POST("auth/login")
    suspend fun authLogin(@Body auth: Auth): Response<User>

    //Get refresh token
    @POST("token")
    suspend fun getRefreshToken(@Body google : google_input) : Response<Resutl_RefreshToken>

    //Login with google
    @POST("auth/google")
    suspend fun loginWithGoogle(@Body lg : loginWithGoogle) : Response<User>

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

}