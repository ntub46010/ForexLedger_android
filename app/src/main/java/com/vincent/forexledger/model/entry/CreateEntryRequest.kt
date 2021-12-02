package com.vincent.forexledger.model.entry

import java.util.Date

data class CreateEntryRequest(var bookId: String,
                              var transactionType: TransactionType,
                              var transactionDate: Long,
                              var foreignAmount: Double,
                              var twdAmount: Int?,
                              var relatedBookId: String?,
                              var relatedBookForeignAmount: Double?)
