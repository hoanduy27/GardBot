package com.example.gardbot

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.gardbot.databinding.ActivityDemoBinding

class DemoActivity : AppCompatActivity() {
    private lateinit var retrofitClient: RetrofitClient
    private lateinit var binding: ActivityDemoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_demo)

        retrofitClient = RetrofitClient()

        binding = ActivityDemoBinding.inflate(layoutInflater)

        Log.e("Helo", "f")
        binding.switch1.setOnCheckedChangeListener { buttonView, isChecked ->
            var sendval = if(isChecked){
                "0"
            }
            else{
                "1"
            }
            Log.e("send", sendval)
            retrofitClient.uploadValue(sendval)
        }
    }
}