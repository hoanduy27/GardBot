package com.example.gardbot

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class RetrofitClient {
    private var BASE_URL = "http://127.0.0.1:5000/"
    private var retrofit: Retrofit? = null

    public fun getClient(url: String): Retrofit {
        retrofit = Retrofit.Builder()
            .baseUrl(url)
            .build()
        return retrofit!!
    }

    public fun uploadValue(value: String) {
        val flaskMqttApi = retrofit?.create(FlaskMqttApi::class.java)
        val call = flaskMqttApi?.uploadValue(value)
        call?.enqueue(object: Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void> )
            {
                // Xử lý response server trả về khi sign up ở đây
            }
            override fun onFailure(call: Call<Void>?, t: Throwable?) {
                Log.v("retrofit", "call failed")
            }
        })
    }

    init {
        getClient(BASE_URL)
    }
}