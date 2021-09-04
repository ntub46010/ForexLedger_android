package com.vincent.forexledger.model.exchangerate

data class ExchangeRateVO(var currencyType: CurrencyType,
                          var sellingRate: Double,
                          var buyingRate: Double)
