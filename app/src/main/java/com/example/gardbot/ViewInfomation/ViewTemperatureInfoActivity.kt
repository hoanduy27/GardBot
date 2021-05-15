package com.example.gardbot.ViewInfomation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView
import android.widget.TextView
import com.example.gardbot.Adapter.CustomSoilDetailAdapter
import com.example.gardbot.Model.SoilHistory
import com.example.gardbot.R
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ViewTemperatureInfoActivity : AppCompatActivity() {
    var temperatureHistoryList = ArrayList<SoilHistory>()
    var select : SoilHistory? = null
    var selectPosition : Int? = null
    lateinit var adapter : CustomSoilDetailAdapter
    lateinit var listViewTemperatureHistory : ListView

    val database = Firebase.database
    val myRef = database.getReference().child("history").child("temperature")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_temperature_info)

        var textCurTemperature = findViewById<TextView>(R.id.textCurTemperature)
        database.getReference().child("sensor")
            .child("rtt").child("1").child("temperature").addValueEventListener(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    textCurTemperature.text = snapshot.value.toString() + "°C"
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })


        listViewTemperatureHistory = findViewById<ListView>(R.id.listViewTemperatureHistory)
        adapter = CustomSoilDetailAdapter(this, R.layout.custom_simple_list_element, temperatureHistoryList)
        listViewTemperatureHistory.adapter = adapter

        myRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                var time = snapshot.key
                var value = snapshot.child("value").getValue(String::class.java)
                temperatureHistoryList.add(SoilHistory(time!!, value!!))
                adapter.notifyDataSetChanged()
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                temperatureHistoryList.clear()
                myRef.removeEventListener(this)
                myRef.addChildEventListener(this)
                adapter.notifyDataSetChanged()
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                temperatureHistoryList.clear()
                myRef.removeEventListener(this)
                myRef.addChildEventListener(this)
                adapter.notifyDataSetChanged()
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}