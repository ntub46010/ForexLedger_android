package com.vincent.forexledger.network

import com.vincent.forexledger.model.bank.BankType
import com.vincent.forexledger.model.exchangerate.ExchangeRateVO
import io.reactivex.rxjava3.core.Single
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ExchangeRateApi {

    @GET("/exchange-rates")
    fun getExchangeRate(@Query("bank") bank: BankType): Single<Response<List<ExchangeRateVO>>>

}