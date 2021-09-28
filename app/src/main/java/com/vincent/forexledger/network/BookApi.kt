package com.vincent.forexledger.network

import com.vincent.forexledger.model.book.CreateBookRequest
import io.reactivex.rxjava3.core.Single
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface BookApi {

    @POST("/books")
    fun createBook(@Body request: CreateBookRequest): Single<Response<Unit>>
}