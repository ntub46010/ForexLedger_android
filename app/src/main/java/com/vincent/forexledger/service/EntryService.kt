package com.vincent.forexledger.service

import com.vincent.forexledger.model.entry.CreateEntryRequest
import com.vincent.forexledger.model.entry.EntryListVO
import com.vincent.forexledger.network.NetworkClient
import com.vincent.forexledger.utils.ResponseCallback
import com.vincent.forexledger.utils.RetrofitUtils

object EntryService {

    fun createEntry(
            request: CreateEntryRequest,
            callback: ResponseCallback<Unit, String>) {

        val entryApi = NetworkClient.entryAPI()
        RetrofitUtils.sendRequest(
                entryApi.createEntry(request),
                callback
        )
    }

    fun loadEntries(
            bookId: String,
            callback: ResponseCallback<List<EntryListVO>, String>) {

        val entryApi = NetworkClient.entryAPI();
        RetrofitUtils.sendRequest(
                entryApi.loadEntries(bookId),
                callback
        )
    }

}