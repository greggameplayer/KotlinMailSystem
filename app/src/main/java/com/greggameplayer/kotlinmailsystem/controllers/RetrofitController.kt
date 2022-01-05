package com.greggameplayer.kotlinmailsystem.controllers

import com.greggameplayer.kotlinmailsystem.interfaces.RetrofitService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

// Class for creating Retrofit instances and providing them to the rest of the application
@Singleton
class RetrofitController {

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://mailsystem.gregoire.live/")
        .addConverterFactory(GsonConverterFactory.create())
        .build() // create the Retrofit instance

    val service = retrofit.create(RetrofitService::class.java) // create the service
}
