package com.example.datn.utils.notification

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Client {
    object Client {
        private var retrofit: Retrofit? = null
        private val interceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        fun getClient(url: String?): Retrofit? {
            if (retrofit == null) {
                val client = OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .build()

                retrofit = url?.let {
                    Retrofit.Builder()
                        .baseUrl(it)
                        .client(client)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                }
            }
            return retrofit
        }
    }
}
