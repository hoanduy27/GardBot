package com.example.gardbot

import android.content.Context
import android.util.Log
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*
import java.nio.charset.Charset

class MqttService(context: Context?) {
    val serverUri = "tcp://io.adafruit.com:1883"
    val clientId = "BBC_LED"
    val subscriptionTopic = "hoanduy27/feeds/bbc-led"
    val username = "hoanduy27"
    val password = "aio_bwga70n3FDP3jtKqWriVuE10odEf"

    var mqttAndroidClient: MqttAndroidClient

    fun setCallback(callback: MqttCallbackExtended?) {
        mqttAndroidClient.setCallback(callback)
    }

    private fun connect() {
        val mqttConnectOptions = MqttConnectOptions()
        mqttConnectOptions.isAutomaticReconnect = true
        mqttConnectOptions.isCleanSession = false
        mqttConnectOptions.userName = username
        mqttConnectOptions.password = password.toCharArray()
        try {
            mqttAndroidClient.connect(mqttConnectOptions, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken) {
                    val disconnectedBufferOptions = DisconnectedBufferOptions()
                    disconnectedBufferOptions.isBufferEnabled = true
                    disconnectedBufferOptions.bufferSize = 100
                    disconnectedBufferOptions.isPersistBuffer = false
                    disconnectedBufferOptions.isDeleteOldestMessages = false
                    mqttAndroidClient.setBufferOpts(disconnectedBufferOptions)
                    subscribeToTopic()
                    Log.w("Mqtt", "Connect successfully")
                }

                override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                    Log.d("Mqtt", "Failed to connect to:$serverUri$exception")
                }
            })
        } catch (ex: MqttException) {
            ex.printStackTrace()
        }
    }

    private fun subscribeToTopic() {
        try {
            mqttAndroidClient.subscribe(subscriptionTopic, 0, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken) {
                    Log.w("Mqtt", "Subscribed!")
                }

                override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                    Log.w("Mqtt", "Subscribed failed!")
                }
            })
        } catch (ex: MqttException) {
            System.err.println("Exceptions to subscribing")
            ex.printStackTrace()
        }
    }

    fun sendDataMqtt(data : String){
        val msg = MqttMessage()
        msg.id = 1234
        msg.qos = 0
        msg.isRetained = true
        val b : ByteArray = data.toByteArray(Charset.forName("UTF-8"))
        msg.payload = b
        Log.d("MQTT", "Publish: $msg")
        try{
            mqttAndroidClient.publish(subscriptionTopic, msg);
        }catch (e: MqttException){
            Log.d("MQTT", "sendDataMQTT: cannot send msg")
        }
    }

    init {
        mqttAndroidClient = MqttAndroidClient(context, serverUri, clientId)
        mqttAndroidClient.setCallback(object : MqttCallbackExtended {
            override fun connectComplete(b: Boolean, s: String) {
                Log.w("Mqtt", s)
            }

            override fun connectionLost(throwable: Throwable) {}
            @Throws(Exception::class)
            override fun messageArrived(topic: String, message: MqttMessage) {
                Log.w("Mqtt", message.toString())
            }

            override fun deliveryComplete(iMqttDeliveryToken: IMqttDeliveryToken) {}
        })
        connect()
    }
}


