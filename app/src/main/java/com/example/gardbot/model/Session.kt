package com.example.gardbot.model

import com.example.gardbot.MqttService

class Session {
    companion object{
        lateinit var username : String
        lateinit var sysID: String
        var mqttURI = "https://bkgardbot.herokuapp.com"
    }
}