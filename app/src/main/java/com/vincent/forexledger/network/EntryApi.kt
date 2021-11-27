package com.vincent.forexledger.network

import com.vincent.forexledger.Constants
import com.vincent.forexledger.DataStorage
import com.vincent.forexledger.model.entry.CreateEntryRequest
import io.reactivex.rxjava3.core.Single
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface EntryApi {

    @POST("/entries")
    fun createEntry(
            @Body request: CreateEntryRequest,
            @Header(Constants.HEADER_AUTHORIZATION) token: String = DataStorage.accessToken
    ): Single<Response<Unit>>
}