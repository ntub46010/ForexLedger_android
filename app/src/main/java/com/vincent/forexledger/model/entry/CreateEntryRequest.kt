package com.vincent.forexledger.model.entry

import java.util.Date

data class CreateEntryRequest(var bookId: String,
                              var transactionType: TransactionType,
                              var transactionDate: Date,
                              var foreignAmount: Double,
                              var twdAmount: Double?,
                              var anotherBookId: String?)
