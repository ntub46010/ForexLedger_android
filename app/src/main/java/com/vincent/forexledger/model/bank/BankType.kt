package com.vincent.forexledger.model.bank

import com.vincent.forexledger.R
import com.vincent.forexledger.model.exchangerate.CurrencyType

enum class BankType(val localNameResource: Int,
                    val supportedCurrency: List<CurrencyType>) {
    FUBON(R.string.bank_fubon, listOf(
            CurrencyType.USD,
            CurrencyType.CNY,
            CurrencyType.JPY,
            CurrencyType.EUR,
            CurrencyType.HKD,
            CurrencyType.AUD,
            CurrencyType.ZAR,
            CurrencyType.CAD,
            CurrencyType.GBP,
            CurrencyType.SGD,
            CurrencyType.CHF,
            CurrencyType.NZD,
            CurrencyType.SEK,
            CurrencyType.THB
    )),

    RICHART(R.string.bank_richart, listOf(
            CurrencyType.USD,
            CurrencyType.CNY,
            CurrencyType.JPY,
            CurrencyType.EUR,
            CurrencyType.HKD,
            CurrencyType.AUD,
            CurrencyType.ZAR,
            CurrencyType.CAD,
            CurrencyType.GBP,
            CurrencyType.SGD,
            CurrencyType.CHF,
            CurrencyType.NZD,
            CurrencyType.SEK
    ));

    fun isSupportedCurrency(type: CurrencyType) = supportedCurrency.contains(type)
}