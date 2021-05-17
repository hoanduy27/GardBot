package com.example.gardbot

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class RetrofitClient {
    private var BASE_URL = "http://10.0.2.2:5000/"
    private var retrofit: Retrofit? = null

    public fun getClient(url: String) {
        this.retrofit = Retrofit.Builder()
            .baseUrl(url)
            .build()
    }

    public fun uploadValue(value: String) {
        val flaskMqttApi = this.retrofit?.create(FlaskMqttApi::class.java)
        val call = flaskMqttApi?.uploadValue(value)
        call?.enqueue(object: Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void> )
            {
                // Xử lý response server trả về
            }
            override fun onFailure(call: Call<Void>?, t: Throwable?) {
                Log.v("retrofit", t.toString())
            }
        })
    }

    init {
        getClient(this.BASE_URL)
    }
}