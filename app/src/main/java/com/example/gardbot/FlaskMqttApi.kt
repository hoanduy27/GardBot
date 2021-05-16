package com.example.gardbot

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface FlaskMqttApi {
    @POST("test")
    fun createPost(@Body post: String): Call<String>
}