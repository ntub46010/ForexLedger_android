package com.vincent.forexledger.service

import com.vincent.forexledger.model.bank.BankType
import com.vincent.forexledger.model.exchangerate.ExchangeRateVO
import com.vincent.forexledger.network.NetworkClient
import com.vincent.forexledger.network.ResponseEntity
import com.vincent.forexledger.utils.RetrofitUtils
import com.vincent.forexledger.utils.SimpleCallback

object ExchangeRateService {

    fun getExchangeRate(
            bank: BankType,
            callback: SimpleCallback<ResponseEntity<List<ExchangeRateVO>>, String>) {

        val exchangeRateApi = NetworkClient.exchangeRateAPI()
        RetrofitUtils.sendRequest(
            exchangeRateApi.getExchangeRate(bank),
            callback
        )
    }
}