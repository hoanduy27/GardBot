package com.example.gardbot

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.gardbot.databinding.ActivityDemoBinding


class DemoActivity : AppCompatActivity() {

    private lateinit var retrofitClient: RetrofitClient
    private lateinit var binding: ActivityDemoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_demo)



        binding = ActivityDemoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        retrofitClient = RetrofitClient()

        binding.btnConnect.setOnClickListener {
            val protocol = if(binding.https.isChecked){
                "https"
            } else {"http"}
            val ip = binding.edtIp.text.toString()
            val port = binding.edtPort.text.toString()
            if(ip != "" && port != ""){
                retrofitClient.getClient("$protocol://$ip:$port")
            }
            else{
                retrofitClient.getClient(retrofitClient.getLocalHostUrl())
            }
            Toast.makeText(this, "Connect to server ${retrofitClient.getBaseUrl()}", Toast.LENGTH_SHORT).show()
        }

        binding.switch1.setOnCheckedChangeListener { buttonView, isChecked ->
            val feed_id = binding.switch1.text.toString()
            Log.e("check is", isChecked.toString())
            val sendval = if(isChecked){
                "1"
            }
            else{
                "0"
            }
            Log.e("send", sendval)
            retrofitClient.uploadValue(feed_id, sendval)
        }

        binding.switch2.setOnCheckedChangeListener { buttonView, isChecked ->
            val feed_id = binding.switch2.text.toString()
            Log.e("check is", isChecked.toString())
            val sendval = if(isChecked){
                "1"
            }
            else{
                "0"
            }
            Log.e("send", sendval)
            retrofitClient.uploadValue(feed_id, sendval)
        }

        binding.switch3.setOnCheckedChangeListener { buttonView, isChecked ->
            val feed_id = binding.switch3.text.toString()
            Log.e("check is", isChecked.toString())
            val sendval = if(isChecked){
                "1"
            }
            else{
                "0"
            }
            Log.e("send", sendval)
            retrofitClient.uploadValue(feed_id, sendval)
        }
    }
}