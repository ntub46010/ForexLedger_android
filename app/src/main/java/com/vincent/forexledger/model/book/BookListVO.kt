package com.vincent.forexledger.model.book

import com.vincent.forexledger.model.exchangerate.CurrencyType

class BookListVO(var id: String,
                 var name: String,
                 var currencyType: CurrencyType,
                 var balance: Double,
                 var twdProfit: Int,
                 var profitRate: Double) {
}