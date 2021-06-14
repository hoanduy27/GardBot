package com.example.gardbot.viewInfomation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import com.example.gardbot.adapters.CustomSoilDetailAdapter
import com.example.gardbot.model.SoilHistory
import com.example.gardbot.model.SoilSensor
import com.example.gardbot.R
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ViewSoilDetailActivity : AppCompatActivity() {
    var soilHistoryList = ArrayList<SoilHistory>()
    var select : SoilHistory? = null
    var selectPosition : Int? = null
    lateinit var adapter : CustomSoilDetailAdapter
    lateinit var listViewSoilHistory : ListView

    val database = Firebase.database
    var myRef = database.getReference().child("history")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_soil_detail)

        var textCurSoil = findViewById<TextView>(R.id.textCurSoil)
        val curSoil = intent.getSerializableExtra("cur_soil") as SoilSensor
        myRef = myRef.child("moisture").child(curSoil.key.toString())
//        Log.d("---abcdef--------", myRef.toString())
        textCurSoil.text = curSoil.moisture

        if (curSoil.moisture!!.toInt() < 10)
            findViewById<LinearLayout>(R.id.linearLayoutCurSoil).setBackgroundResource(R.drawable.custom_info_warning)

        listViewSoilHistory = findViewById<ListView>(R.id.listViewSoilHistory)
        adapter = CustomSoilDetailAdapter(this, R.layout.custom_simple_list_element, soilHistoryList)
        listViewSoilHistory.adapter = adapter

        myRef.addChildEventListener(object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
//                Log.d("xzy", "hmm")
                var time = snapshot.key
                var value = snapshot.child("value").getValue(String::class.java)
//                Log.d("----child add-----", time.toString() + value.toString())
                soilHistoryList.add(SoilHistory(time!!, value!!))
                adapter.notifyDataSetChanged()
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                soilHistoryList.clear()
                myRef.removeEventListener(this)
                myRef.addChildEventListener(this)
                adapter.notifyDataSetChanged()
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                soilHistoryList.clear()
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