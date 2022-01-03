package com.greggameplayer.kotlinmailsystem.interfaces

import com.greggameplayer.kotlinmailsystem.beans.MailboxBean
import com.greggameplayer.kotlinmailsystem.beans.RetrofitResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface RetrofitService {
    @POST("/verify-mailbox")
    suspend fun verifyMailbox(@Body mailboxBean: MailboxBean): RetrofitResponse

    @POST("/create-mailbox")
    suspend fun createMailbox(@Body mailboxBean: MailboxBean): RetrofitResponse

    @POST("/modify-mailbox")
    suspend fun modifyMailbox(@Body mailboxBean: MailboxBean): RetrofitResponse
}
