package com.example.gardbot.viewInfomation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView
import com.example.gardbot.adapters.CustomCurSoilAdapter
import com.example.gardbot.model.SoilSensor
import com.example.gardbot.R
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ViewSoilInfoActivity : AppCompatActivity() {
    var curSoilList = ArrayList<SoilSensor>()
    lateinit var adapter : CustomCurSoilAdapter
    var select : SoilSensor? = null
    var selectPosition : Int? = null

    val database = Firebase.database
    val myRef = database.getReference().child("sensor").child("soilMoisture")

    lateinit var listViewCurSoil : ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_soil_info)

        listViewCurSoil = findViewById<ListView>(R.id.listViewCurSoil)
        adapter = CustomCurSoilAdapter(this, R.layout.custom_soil_history_element, curSoilList)
        listViewCurSoil.adapter = adapter

        myRef.addChildEventListener(object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                var cur_soil : SoilSensor = snapshot.getValue(SoilSensor::class.java)!!
                cur_soil.key = snapshot.key
                curSoilList.add(cur_soil)
                adapter.notifyDataSetChanged()
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                curSoilList.clear()
                myRef.removeEventListener(this)
                myRef.addChildEventListener(this)
                adapter.notifyDataSetChanged()
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                curSoilList.clear()
                myRef.removeEventListener(this)
                myRef.addChildEventListener(this)
                adapter.notifyDataSetChanged()
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

        listViewCurSoil.setOnItemClickListener { adapter, view, position, id ->
            select = curSoilList[position]
            selectPosition = position

            val intent = Intent(this, ViewSoilDetailActivity::class.java)
            intent.putExtra("cur_soil", select)
            startActivity(intent)
        }
    }
}