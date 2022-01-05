package com.greggameplayer.kotlinmailsystem.interfaces

import com.greggameplayer.kotlinmailsystem.beans.MailboxBean
import com.greggameplayer.kotlinmailsystem.beans.RetrofitResponse
import retrofit2.http.Body
import retrofit2.http.POST

// Interface for Retrofit which used to communicate with the API
interface RetrofitService {
    @POST("/verify-mailbox")
    suspend fun verifyMailbox(@Body mailboxBean: MailboxBean): RetrofitResponse

    @POST("/create-mailbox")
    suspend fun createMailbox(@Body mailboxBean: MailboxBean): RetrofitResponse

    @POST("/modify-mailbox")
    suspend fun modifyMailbox(@Body mailboxBean: MailboxBean): RetrofitResponse
}
