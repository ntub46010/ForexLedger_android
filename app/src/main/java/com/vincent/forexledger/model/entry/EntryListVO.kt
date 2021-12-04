package com.vincent.forexledger.model.entry

import com.vincent.forexledger.model.exchangerate.CurrencyType
import java.util.Date

data class EntryListVO(var id: String,
                       var transactionDate: Date,
                       var primaryAmount: Double,
                       var primaryCurrencyType: CurrencyType,
                       var relatedAmount: Double?,
                       var relatedCurrencyType: CurrencyType?,
                       var description: String?)
