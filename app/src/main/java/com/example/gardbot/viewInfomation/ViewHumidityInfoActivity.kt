package com.example.gardbot.viewInfomation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView
import android.widget.TextView
import com.example.gardbot.adapters.CustomSoilDetailAdapter
import com.example.gardbot.model.SoilHistory
import com.example.gardbot.R
import com.example.gardbot.model.Session
import com.example.gardbot.utils.DateFormatUtils
import com.example.gardbot.utils.TimeseriesUtils
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.utils.EntryXComparator
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.security.KeyStore
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ViewHumidityInfoActivity : AppCompatActivity() {

    var humidityHistoryList = ArrayList<SoilHistory>()
    var select : SoilHistory? = null
    var selectPosition : Int? = null
    lateinit var adapter : CustomSoilDetailAdapter
    lateinit var listViewHumidityHistory : ListView
    lateinit var chartViewHumidityHistory : LineChart

    //For offsets from start timestamp, since float (32 bit) cannot store timestamps accurately
    var entries = ArrayList<Entry>()
    //For timestamps
    var timeData = ArrayList<Long>()

    val database = Firebase.database
    val myRef = database.getReference().child("history").child("humidity")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_humidity_info)

        var textCurHumidity = findViewById<TextView>(R.id.textCurHumidity)
        database.reference.child("sensor")
            .child("dht").addValueEventListener(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach{
                        if(it.child("sysID").value.toString() == Session.sysID){
                            textCurHumidity.text = it.child("humidity").value.toString()
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })


        listViewHumidityHistory = findViewById<ListView>(R.id.listViewHumidityHistory)
        chartViewHumidityHistory = findViewById<LineChart>(R.id.chartViewHumidHistory)
        adapter = CustomSoilDetailAdapter(this, R.layout.custom_simple_list_element, humidityHistoryList, "%")
        listViewHumidityHistory.adapter = adapter
        database.reference.addValueEventListener(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    humidityHistoryList.clear()
                    var dht = ""
                    snapshot.child("sensor").child("dht").children.forEach {
                        if (it.child("sysID").value.toString() == Session.sysID) {
                            dht = it.key!!
                        }
                    }
                    // View humidity logs
                    myRef.child(dht).addChildEventListener(object : ChildEventListener{
                        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                            var time = DateFormatUtils.datetimeFormat.format(snapshot.key!!.toLong()*1000)
                            var value = snapshot.child("value").getValue(String::class.java)
                            humidityHistoryList.add(SoilHistory(time!!, value!!))
                            adapter.notifyDataSetChanged()
                        }

                        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
//                            humidityHistoryList.clear()
//                            myRef.removeEventListener(this)
//                            myRef.addChildEventListener(this)
//                            adapter.notifyDataSetChanged()
                        }

                        override fun onChildRemoved(snapshot: DataSnapshot) {
//                            humidityHistoryList.clear()
//                            myRef.removeEventListener(this)
//                            myRef.addChildEventListener(this)
//                            adapter.notifyDataSetChanged()
                        }

                        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                        }

                        override fun onCancelled(error: DatabaseError) {
                        }
                    })

                    //Draw timeseries
                    myRef.child(dht).addValueEventListener(object : ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            snapshot.children.forEach{
                                entries.clear()
                                timeData.clear()
                                var offset: Long
                                snapshot.children.forEach{
                                    val time = it.key!!.toLong()
                                    val value = it.child("value").value.toString()
                                    timeData.add(time)
                                    offset = time - timeData[0]
                                    entries.add(Entry(offset.toFloat(), value.toFloat()))
                                }
                                Collections.sort(entries, EntryXComparator())

                                chartViewHumidityHistory.notifyDataSetChanged()
                                chartViewHumidityHistory.invalidate()
                                TimeseriesUtils.drawChart(
                                    chart = chartViewHumidityHistory,
                                    context = applicationContext,
                                    offsets = entries,
                                    startTime = timeData[0],
                                    yAxisMin = 0f, yAxisMax = 100f
                                )
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }
                    })
                }


                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })

    }
}