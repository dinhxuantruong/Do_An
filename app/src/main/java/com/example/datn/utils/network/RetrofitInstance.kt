package com.example.datn.utils.network


import com.example.datn.service.AdminApi
import com.example.datn.service.myApi
import com.example.datn.utils.network.Constance.Companion.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


object RetrofitInstance {

    var Token = ""

    private val interceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(interceptor)
        // Thêm timeout cho OkHttpClient
        .callTimeout(30, TimeUnit.SECONDS) // Thời gian tối đa để hoàn thành cuộc gọi
        .connectTimeout(5, TimeUnit.SECONDS) // Thời gian tối đa để kết nối với máy chủ
        .readTimeout(30, TimeUnit.SECONDS) // Thời gian tối đa để đọc dữ liệu từ máy chủ
        .addInterceptor { chain ->
            val request = chain.request().newBuilder().addHeader("Authorization", Token).build()
            chain.proceed(request)
        }
        .build()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val appApi: myApi by lazy {
        retrofit.create(myApi::class.java)
    }

    val adminApi: AdminApi by lazy {
        retrofit.create(AdminApi::class.java)
    }
}

