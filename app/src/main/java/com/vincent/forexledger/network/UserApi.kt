package com.vincent.forexledger.network

import com.vincent.forexledger.request.UserRequest
import com.vincent.forexledger.response.UserResponse
import io.reactivex.rxjava3.core.Single
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface UserApi {

    @POST("users")
    fun createUser(@Body request: UserRequest): Single<Response<UserResponse>>
}