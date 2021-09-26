package com.vincent.forexledger.service

import com.vincent.forexledger.model.book.CreateBookRequest
import com.vincent.forexledger.network.NetworkClient
import com.vincent.forexledger.utils.ResponseCallback
import com.vincent.forexledger.utils.RetrofitUtils

object BookService {

    fun createBook(
            request: CreateBookRequest,
            callback: ResponseCallback<Unit, String>) {

        val bookApi = NetworkClient.bookAPI()
        RetrofitUtils.sendRequest(
                bookApi.createBook(request),
                callback
        )
    }
}