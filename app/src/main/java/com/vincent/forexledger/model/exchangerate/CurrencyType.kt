package com.vincent.forexledger.model.exchangerate

import com.vincent.forexledger.R

enum class CurrencyType(val localNameResource: Int,
                        val imageResource: Int) {
    // TODO: specify image resource
    USD(R.string.currency_usd, 0),
    CNY(R.string.currency_cny, 0),
    JPY(R.string.currency_jpy, 0)
}