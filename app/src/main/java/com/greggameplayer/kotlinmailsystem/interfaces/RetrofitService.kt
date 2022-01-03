package com.greggameplayer.kotlinmailsystem.interfaces

import com.greggameplayer.kotlinmailsystem.beans.RetrofitResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface RetrofitService {
    @POST("/verify-mailbox")
    suspend fun verifyMailbox(@Body username: String, @Body password: String): Call<RetrofitResponse>

    @POST("/create-mailbox")
    suspend fun createMailbox(@Body username: String, @Body password: String, @Body name: String): Call<RetrofitResponse>

    @POST("/modify-mailbox")
    suspend fun modifyMailbox(@Body username: String, @Body password: String, @Body name: String): Call<RetrofitResponse>
}
