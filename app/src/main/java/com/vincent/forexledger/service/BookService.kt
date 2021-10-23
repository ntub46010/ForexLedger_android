package com.vincent.forexledger.service

import com.vincent.forexledger.model.book.BookDetailVO
import com.vincent.forexledger.model.book.BookListVO
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

    fun loadMyBooks(
            callback: ResponseCallback<List<BookListVO>, String>) {

        val bookApi = NetworkClient.bookAPI()
        RetrofitUtils.sendRequest(
                bookApi.loadMyBook(),
                callback
        )
    }

    fun loadBookDetail(
            id: String,
            callback: ResponseCallback<BookDetailVO, String>) {

        val bookApi = NetworkClient.bookAPI()
        RetrofitUtils.sendRequest(
                bookApi.loadBookDetail(id),
                callback
        )
    }
}