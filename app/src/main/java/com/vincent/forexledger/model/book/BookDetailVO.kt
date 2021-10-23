package com.vincent.forexledger.model.book

import com.vincent.forexledger.model.exchangerate.CurrencyType

data class BookDetailVO(var id: String,
                        var currencyType: CurrencyType,
                        var bankBuyingRate: Double,
                        var balance: Double,
                        var twdCurrentValue: Int,
                        var twdProfit: Int?,
                        var twdProfitRate: Double?,
                        var breakEvenPoint: Double?,
                        var lastForeignIngest: Double?,
                        var lastTwdInvest: Int?,
                        var lastSellingRate: Double?) {
}