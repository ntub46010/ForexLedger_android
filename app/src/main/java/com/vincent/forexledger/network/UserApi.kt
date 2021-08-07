package com.vincent.forexledger.network

import com.vincent.forexledger.request.UserRequest
import com.vincent.forexledger.response.UserResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface UserApi {

    @POST("users")
    fun createUser(@Body request: UserRequest): Call<UserResponse>
}