package com.vincent.forexledger.network

import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NetworkClient {
    private const val SERVER_IP = "http://192.168.42.210:8080/"
    private val retrofit: Retrofit

    private var exchangeRateApi: ExchangeRateApi? = null
    private var bookApi: BookApi? = null
    private var entryApi: EntryApi? = null

    init {
        val client = OkHttpClient.Builder()
            .readTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(SERVER_IP)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.createWithScheduler(Schedulers.io()))
            .client(client)
            .build()
    }

    fun exchangeRateAPI(): ExchangeRateApi {
        if (exchangeRateApi == null) {
            exchangeRateApi = retrofit.create(ExchangeRateApi::class.java)
        }
        return exchangeRateApi!!
    }

    fun bookAPI(): BookApi {
        if (bookApi == null) {
            bookApi = retrofit.create(BookApi::class.java)
        }
        return bookApi!!;
    }

    fun entryAPI(): EntryApi {
        if (entryApi == null) {
            entryApi = retrofit.create(EntryApi::class.java)
        }
        return entryApi!!
    }
}