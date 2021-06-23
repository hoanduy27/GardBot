package com.example.gardbot.viewInfomation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.gardbot.R
import com.example.gardbot.model.Session
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ViewInfomationActivity : AppCompatActivity() {
    lateinit var textViewDetail_soil : TextView
    lateinit var textViewDetail_humidity : TextView
    lateinit var textViewDetail_temperature : TextView
    lateinit var textViewDetail_pump : TextView
    val database = Firebase.database

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_infomation)

        textViewDetail_soil = findViewById<TextView>(R.id.textViewDetail_Soil)
        textViewDetail_humidity = findViewById<TextView>(R.id.textViewDetail_Humidity)
        textViewDetail_temperature = findViewById<TextView>(R.id.textViewDetail_Temperature)
        textViewDetail_pump = findViewById<TextView>(R.id.textViewDetail_Pump)

        var textCurHumidity = findViewById<TextView>(R.id.textView_CurrentHumidity)
        var textCurTemperature = findViewById<TextView>(R.id.textView_CurrentTemperature)
        var textCurActivePump = findViewById<TextView>(R.id.textView_CurrentActivePump)
        database.reference.child("sensor").child("dht").addValueEventListener(object:
            ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    if(it.child("sysID").value.toString() == Session.sysID){
                        textCurHumidity.text = it.child("humidity").value.toString() + "%"
                        textCurTemperature.text = it.child("temperature").value.toString() + "C"
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })



        database.reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var num_active = 0;
                var numPumps = 0
                snapshot.child("pump").children.forEach {
                    val soilMoistureID = it.child("soilMoistureID").value.toString()
                    if(snapshot.child("sensor/soilMoisture").child(soilMoistureID).child("sysID").value.toString() == Session.sysID){
                        numPumps++
                        if (it.child("waterLevel").value != "0")
                            num_active++
                    }
                }
                textCurActivePump.text = "active: ${num_active.toString()}/${numPumps.toString()}"
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })


        textViewDetail_soil.setOnClickListener{
            moveToSoilInfo();
        };
        textViewDetail_humidity.setOnClickListener{
            moveToHumidityInfo();
        };
        textViewDetail_temperature.setOnClickListener{
            moveToTemperatureInfo();
        };
        textViewDetail_pump.setOnClickListener{
            moveToPumpInfo();
        };
    }

    fun moveToSoilInfo(){
        startActivity(Intent(this, ViewSoilInfoActivity::class.java));
    }

    fun moveToHumidityInfo(){
        startActivity(Intent(this, ViewHumidityInfoActivity::class.java));
    }

    fun moveToPumpInfo(){
        startActivity(Intent(this, ViewPumpInfoActivity::class.java));
    }

    fun moveToTemperatureInfo(){
        startActivity(Intent(this, ViewTemperatureInfoActivity::class.java))
    }
}