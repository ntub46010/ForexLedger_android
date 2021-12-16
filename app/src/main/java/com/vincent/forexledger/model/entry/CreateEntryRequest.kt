package com.vincent.forexledger.model.entry

data class CreateEntryRequest(var bookId: String,
                              var transactionType: TransactionType,
                              var transactionDate: Long,
                              var description: String?,
                              var foreignAmount: Double,
                              var twdAmount: Int?,
                              var relatedBookId: String?,
                              var relatedBookForeignAmount: Double?)
