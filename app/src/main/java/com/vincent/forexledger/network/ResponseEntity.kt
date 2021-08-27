package com.vincent.forexledger.network

import io.reactivex.rxjava3.disposables.Disposable
import retrofit2.Response

data class ResponseEntity<T>(val response: Response<T>,
                             val disposables: List<Disposable>) {

    fun getStatusCode() = response.code()
    fun getHeader(name: String) = response.headers()[name]
    fun getBody() = response.body()
}