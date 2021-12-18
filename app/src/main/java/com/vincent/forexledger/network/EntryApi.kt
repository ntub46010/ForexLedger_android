package com.vincent.forexledger.network

import com.vincent.forexledger.Constants
import com.vincent.forexledger.DataStorage
import com.vincent.forexledger.model.entry.CreateEntryRequest
import com.vincent.forexledger.model.entry.EntryListVO
import io.reactivex.rxjava3.core.Single
import retrofit2.Response
import retrofit2.http.*

interface EntryApi {

    @POST("/entries")
    fun createEntry(
            @Body request: CreateEntryRequest,
            @Header(Constants.HEADER_AUTHORIZATION) token: String = DataStorage.accessToken
    ): Single<Response<Unit>>

    @GET("/entries")
    fun loadEntries(
            @Query("bookId") bookId: String,
            @Header(Constants.HEADER_AUTHORIZATION) token: String = DataStorage.accessToken
    ): Single<Response<List<EntryListVO>>>
}