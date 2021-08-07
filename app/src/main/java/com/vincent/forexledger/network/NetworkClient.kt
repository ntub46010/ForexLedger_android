package com.vincent.forexledger.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NetworkClient {
    private val retrofit: Retrofit
    private var userApi: UserApi? = null

    init {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder()
            .readTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(interceptor).build()

        val factory = GsonConverterFactory.create()
        retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.42.210:8080/")
            .addConverterFactory(factory)
            .client(client)
            .build()
    }

    fun userAPI(): UserApi {
        if (userApi == null) {
            userApi = retrofit.create(UserApi::class.java)
        }
        return userApi!!
    }
}