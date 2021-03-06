package com.vincent.forexledger.network

import com.vincent.forexledger.Constants
import com.vincent.forexledger.DataStorage
import com.vincent.forexledger.model.book.BookDetailVO
import com.vincent.forexledger.model.book.BookListVO
import com.vincent.forexledger.model.book.CreateBookRequest
import io.reactivex.rxjava3.core.Single
import retrofit2.Response
import retrofit2.http.*

interface BookApi {

    @POST("/books")
    fun createBook(
            @Body request: CreateBookRequest,
            @Header(Constants.HEADER_AUTHORIZATION) token: String = DataStorage.accessToken
    ): Single<Response<Unit>>

    @GET("/books")
    fun loadMyBook(
            @Header(Constants.HEADER_AUTHORIZATION) token: String = DataStorage.accessToken
    ): Single<Response<List<BookListVO>>>

    @GET("/books/{id}")
    fun loadBookDetail(
            @Path("id") bookId: String,
            @Header(Constants.HEADER_AUTHORIZATION) token: String = DataStorage.accessToken
    ): Single<Response<BookDetailVO>>
}