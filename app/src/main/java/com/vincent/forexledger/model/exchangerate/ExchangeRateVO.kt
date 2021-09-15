package com.vincent.forexledger.model.exchangerate

import java.util.Date

data class ExchangeRateVO(var currencyType: CurrencyType,
                          var sellingRate: Double,
                          var buyingRate: Double,
                          var updatedTime: Date)
