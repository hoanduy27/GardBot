package com.example.gardbot.ViewInfomation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.gardbot.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ViewInfomationActivity : AppCompatActivity() {
    lateinit var textViewDetail_soil : TextView;
    lateinit var textViewDetail_humidity : TextView;
    lateinit var textViewDetail_temperator : TextView;
    lateinit var textViewDetail_pump : TextView;
    val database = Firebase.database

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_infomation)

        textViewDetail_soil = findViewById<TextView>(R.id.textViewDetail_Soil);
        textViewDetail_humidity = findViewById<TextView>(R.id.textViewDetail_Humidity);
        textViewDetail_temperator = findViewById<TextView>(R.id.textViewDetail_Temperature);
        textViewDetail_pump = findViewById<TextView>(R.id.textViewDetail_Pump);

        var textCurHumidity = findViewById<TextView>(R.id.textView_CurrentHumidity)
        database.getReference().child("sensor")
            .child("rtt").child("1").child("humidity").addValueEventListener(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    textCurHumidity.text = snapshot.value.toString() + "%"
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })

        var textCurTemperature = findViewById<TextView>(R.id.textView_CurrentTemperature)
        database.getReference().child("sensor")
            .child("rtt").child("1").child("temperature").addValueEventListener(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    textCurTemperature.text = snapshot.value.toString() + "°C"
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })

        var textCurActivePump = findViewById<TextView>(R.id.textView_CurrentActivePump)
        database.getReference().child("pump").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var num_active = 0;
                    snapshot.children.forEach {
                        if (it.child("waterLevel").value != "0")
                            num_active = num_active + 1;
                    }
                    textCurActivePump.text = "active: " + num_active.toString() + "/" +snapshot.getChildrenCount().toString()
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
        textViewDetail_temperator.setOnClickListener{
            moveToTemperatorInfo();
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

    fun moveToTemperatorInfo(){
        startActivity(Intent(this, ViewTemperatorInfoActivity::class.java))
    }
}