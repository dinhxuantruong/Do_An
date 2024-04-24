package com.example.datn.repository

import android.content.Context
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
import com.example.datn.utils.SharePreference.UserPreferences
import com.example.datn.utils.network.RetrofitGoogle
import com.example.datn.utils.network.RetrofitInstance
import retrofit2.Response

class repositoryAuth(private val context: Context) {


    private val userPreferences = UserPreferences(context)

    suspend fun login(auth: Auth): Response<User> {
        return RetrofitInstance.appApi.authLogin(auth)
    }

    suspend fun saveAccessToken(token: String) {
        userPreferences.saveAuthToken(token)
    }


    suspend fun getRefreshToken(google : google_input) : Response<Resutl_RefreshToken>{
        return RetrofitGoogle.appApi.getRefreshToken(google)
    }

    suspend fun loginWithGoogle(lg : loginWithGoogle) : Response<User>{
        return RetrofitInstance.appApi.loginWithGoogle(lg)
    }

    suspend fun register1(body : Register) : Response<ResultMessage>{
        return RetrofitInstance.appApi.authRegister1(body)
    }

    suspend fun authAcceptRegister(acceptOTP: AcceptOTP): Response<ResultMessage> {
        return RetrofitInstance.appApi.authAcceptRegister(acceptOTP)
    }


    suspend fun authForgetPassword(forgetPass: ForgetPass): Response<ResultMessage> {
        return RetrofitInstance.appApi.authForgetPassword(forgetPass)
    }

    suspend fun authSendOtp(sendOTP: sendOTP): Response<ResultMessage> {
        return RetrofitInstance.appApi.authSendOTP(sendOTP)
    }

    suspend fun authCheckAccount(sendOTP: sendOTP): Response<ResultMessage> {
        return RetrofitInstance.appApi.authCheckAccountForget(sendOTP)
    }

    suspend fun authLogout(): Response<ResultMessage> {
        return RetrofitInstance.appApi.authLogout()
    }
}