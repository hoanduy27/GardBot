package com.example.gardbot

import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface FlaskMqttApi {
    @FormUrlEncoded
    @POST("test")
    fun uploadValue(@Field("feed_id")feed_id: String, @Field("value") value: String): Call<Void>
}