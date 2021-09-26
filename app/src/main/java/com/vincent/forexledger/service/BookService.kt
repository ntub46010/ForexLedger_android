package com.vincent.forexledger.service

import com.vincent.forexledger.model.book.CreateBookRequest
import com.vincent.forexledger.network.NetworkClient
import com.vincent.forexledger.network.ResponseEntity
import com.vincent.forexledger.utils.RetrofitUtils
import com.vincent.forexledger.utils.SimpleCallback

object BookService {

    fun createBook(
            request: CreateBookRequest,
            callback: SimpleCallback<ResponseEntity<Unit>, String>) {
        val bookApi = NetworkClient.bookAPI()
        RetrofitUtils.sendRequest(
                bookApi.createBook(request),
                callback
        )
    }
}