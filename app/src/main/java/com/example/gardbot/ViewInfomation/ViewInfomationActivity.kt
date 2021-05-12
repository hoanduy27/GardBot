package com.example.gardbot.ViewInfomation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.gardbot.R

class ViewInfomationActivity : AppCompatActivity() {
    lateinit var textViewDetail_soil : TextView;
    lateinit var textViewDetail_humidity : TextView;
    lateinit var textViewDetail_temperator : TextView;
    lateinit var textViewDetail_pump : TextView;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_infomation)

        textViewDetail_soil = findViewById<TextView>(R.id.textViewDetail_Soil);
        textViewDetail_humidity = findViewById<TextView>(R.id.textViewDetail_Humidity);
        textViewDetail_temperator = findViewById<TextView>(R.id.textViewDetail_Temperature);
        textViewDetail_pump = findViewById<TextView>(R.id.textViewDetail_Pump);

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