package com.example.gardbot.viewInfomation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView
import android.widget.TextView
import com.example.gardbot.adapters.CustomSoilDetailAdapter
import com.example.gardbot.model.SoilHistory
import com.example.gardbot.R
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ViewHumidityInfoActivity : AppCompatActivity() {

    var humidityHistoryList = ArrayList<SoilHistory>()
    var select : SoilHistory? = null
    var selectPosition : Int? = null
    lateinit var adapter : CustomSoilDetailAdapter
    lateinit var listViewHumidityHistory : ListView

    val database = Firebase.database
    val myRef = database.getReference().child("history").child("humidity")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_humidity_info)

        var textCurHumidity = findViewById<TextView>(R.id.textCurHumidity)
        database.getReference().child("sensor")
            .child("dht").child("dht0001").child("humidity").addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    textCurHumidity.text = snapshot.value.toString() + "%"
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })


        listViewHumidityHistory = findViewById<ListView>(R.id.listViewHumidityHistory)
        adapter = CustomSoilDetailAdapter(this, R.layout.custom_simple_list_element, humidityHistoryList)
        listViewHumidityHistory.adapter = adapter

        myRef.addChildEventListener(object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                var time = snapshot.key
                var value = snapshot.child("value").getValue(String::class.java)
                humidityHistoryList.add(SoilHistory(time!!, value!!))
                adapter.notifyDataSetChanged()
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                humidityHistoryList.clear()
                myRef.removeEventListener(this)
                myRef.addChildEventListener(this)
                adapter.notifyDataSetChanged()
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                humidityHistoryList.clear()
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