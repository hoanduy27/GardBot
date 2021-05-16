package model

import com.example.gardbot.MqttService

class Session {
    companion object{
        lateinit var username : String
        lateinit var systemID : String
        lateinit var systemName : String
        lateinit var mqtt : MqttService
    }
}