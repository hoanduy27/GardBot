package com.example.gardbot.viewInfomation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.ListView
import com.example.gardbot.Adapter.CustomPumpInfoAdapter
import com.example.gardbot.adapters.CustomSoilDetailAdapter
import com.example.gardbot.Model.Pump
import com.example.gardbot.model.SoilHistory
import com.example.gardbot.model.SoilSensor
import com.example.gardbot.R
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ViewPumpInfoActivity : AppCompatActivity() {

    var pumpList = ArrayList<Pump>()
    var select : Pump? = null
    var selectPosition : Int? = null
    lateinit var adapter : CustomPumpInfoAdapter
    lateinit var listViewPump : ListView
    lateinit var pumpSetting : ImageView

    val database = Firebase.database
    val myRef = database.getReference().child("pump")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_pump_info)

        pumpSetting = findViewById<ImageView>(R.id.imageViewSettingPump)

        listViewPump = findViewById<ListView>(R.id.listViewPump)
        adapter = CustomPumpInfoAdapter(this, R.layout.custom_listview_pump, pumpList)
        listViewPump.adapter = adapter

        myRef.addChildEventListener(object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                var cur_pump : Pump = snapshot.getValue(Pump::class.java)!!
                pumpList.add(cur_pump)
//                Log.d("-------------pump auto--------------", cur_pump.auto!!.toString())
//                Log.d("-------------pump name--------------", cur_pump.name!!.toString())
//                Log.d("-------------pump soil--------------", cur_pump.soilMoistureID!!.toString())
//                Log.d("-------------pump water level--------------", cur_pump.waterLevel!!.toString())
                adapter.notifyDataSetChanged()
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                pumpList.clear()
                myRef.removeEventListener(this)
                myRef.addChildEventListener(this)
                adapter.notifyDataSetChanged()
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
//                pumpList.clear()
//                myRef.removeEventListener(this)
//                myRef.addChildEventListener(this)
//                adapter.notifyDataSetChanged()
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

        pumpSetting.setOnClickListener{
            moveToPumpSetting();
        }
    }

    private fun moveToPumpSetting() {
        intent = Intent(this, ViewInfomationActivity::class.java)
//        intent = Intent(this, ActivityPump::class.java)
//        intent = Intent(this, PumpControlActivity::class.java)
        startActivity(intent)
    }
}