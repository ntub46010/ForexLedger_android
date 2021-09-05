package com.vincent.forexledger.model.exchangerate

import com.vincent.forexledger.R

enum class CurrencyType(val localNameResource: Int,
                        val imageResource: Int) {
    USD(R.string.currency_usd, R.drawable.currency_usd),
    CNY(R.string.currency_cny, R.drawable.currency_cny),
    JPY(R.string.currency_jpy, R.drawable.currency_jpy),
    EUR(R.string.currency_eur, R.drawable.currency_eur),
    HKD(R.string.currency_hkd, R.drawable.currency_hkd),
    AUD(R.string.currency_aud, R.drawable.currency_aud),
    ZAR(R.string.currency_zar, R.drawable.currency_zar),
    CAD(R.string.currency_cad, R.drawable.currency_cad),
    GBP(R.string.currency_gbp, R.drawable.currency_gbp),
    SGD(R.string.currency_sgd, R.drawable.currency_sgd),
    CHF(R.string.currency_chf, R.drawable.currency_chf),
    NZD(R.string.currency_nzd, R.drawable.currency_nzd),
    SEK(R.string.currency_sek, R.drawable.currency_sek),
    THB(R.string.currency_thb, R.drawable.currency_thb)
}