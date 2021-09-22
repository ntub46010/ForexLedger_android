package com.vincent.forexledger.model.book

import com.vincent.forexledger.model.bank.BankType
import com.vincent.forexledger.model.exchangerate.CurrencyType

data class CreateBookRequest(var bookName: String,
                             var bank: BankType,
                             var currencyType: CurrencyType)
