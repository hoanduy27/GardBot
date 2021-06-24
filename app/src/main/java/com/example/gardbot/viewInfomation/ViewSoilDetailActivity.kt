package com.example.gardbot.viewInfomation

import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.gardbot.adapters.CustomSoilDetailAdapter
import com.example.gardbot.model.SoilHistory
import com.example.gardbot.model.SoilSensor
import com.example.gardbot.R
import com.example.gardbot.utils.CustomDateTime
import com.example.gardbot.utils.DateFormatUtils
import com.example.gardbot.utils.TimeseriesUtils
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.utils.EntryXComparator
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ViewSoilDetailActivity : AppCompatActivity() {
    var soilHistoryList = ArrayList<SoilHistory>()
    lateinit var adapter : CustomSoilDetailAdapter
    lateinit var listViewSoilHistory : ListView

    lateinit var chartViewSoilHistory : LineChart
    var entries = ArrayList<Entry>()
    var timeData = ArrayList<Long>()
//    lateinit var dataset : LineDataSet
//    lateinit var data : LineData

    val database = Firebase.database
    var myRef = database.getReference().child("history")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_soil_detail)

        val textCurSoil = findViewById<TextView>(R.id.textCurSoil)
        val curSoil = intent.getSerializableExtra("cur_soil") as SoilSensor
        myRef = myRef.child("moisture").child(curSoil.key.toString())

        if (curSoil.moisture!!.toInt() < 10)
            findViewById<LinearLayout>(R.id.linearLayoutCurSoil).setBackgroundResource(R.drawable.custom_info_warning)

        listViewSoilHistory = findViewById<ListView>(R.id.listViewSoilHistory)
        chartViewSoilHistory = findViewById<LineChart>(R.id.chartViewSoilHistory)
        adapter = CustomSoilDetailAdapter(this, R.layout.custom_simple_list_element, soilHistoryList, "%")
        listViewSoilHistory.adapter = adapter

        database.reference.child("sensor/soilMoisture").child(curSoil.key!!).addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                textCurSoil.text = snapshot.child("moisture").value.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        myRef.addChildEventListener(object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
//                Log.d("xzy", "hmm")
                val time = DateFormatUtils.datetimeFormat.format(snapshot.key!!.toLong()*1000)
                val value = snapshot.child("value").getValue(String::class.java)
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

        // Time-series
        myRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                entries.clear()
                timeData.clear()
                var offset: Long
                snapshot.children.forEach{
                    val time = it.key!!.toLong()
                    val value = it.child("value").value.toString()
                    timeData.add(time)
                    offset = time - timeData[0]
                    entries.add(Entry(offset.toFloat(), value.toFloat()))
//                    dataset.notifyDataSetChanged()
                }
                Collections.sort(entries, EntryXComparator())

                chartViewSoilHistory.notifyDataSetChanged()
                chartViewSoilHistory.invalidate()
                TimeseriesUtils.drawChart(
                    chart = chartViewSoilHistory,
                    context = applicationContext,
                    offsets = entries,
                    startTime = timeData[0],
                    yAxisMin = 0f, yAxisMax = 1023f
                )
                //setupChart()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    fun setupChart(){
        // Setting chart
        var gradient = LinearGradient(0f, 0f, 0f, 600f, Color.GREEN, Color.WHITE, Shader.TileMode.CLAMP)
        chartViewSoilHistory.setTouchEnabled(true)
        chartViewSoilHistory.isDragEnabled = true
        chartViewSoilHistory.setScaleEnabled(true)
        chartViewSoilHistory.setDrawGridBackground(false)
        chartViewSoilHistory.setPinchZoom(false)
        chartViewSoilHistory.description.isEnabled = false
        chartViewSoilHistory.renderer.paintRender.shader = gradient
        // Set data for plotting
        val dataset = LineDataSet(entries, null)
        dataset.fillDrawable = ContextCompat.getDrawable(applicationContext, R.drawable.green_gradient)
        dataset.setDrawFilled(true)

        val data = LineData(dataset)
        data.setValueTextColor(Color.BLACK)
        chartViewSoilHistory.data = data

        // Format legend, xAxis, yAxis
        chartViewSoilHistory.legend.isEnabled = false

        val xi = chartViewSoilHistory.xAxis
        xi.textColor = Color.BLACK
        xi.labelCount = 5
        xi.position = XAxis.XAxisPosition.BOTTOM
        if(timeData.size > 0){
            xi.valueFormatter = CustomDateTime(timeData[0])
        }

        val yi = chartViewSoilHistory.axisLeft
        yi.textColor = Color.BLACK
        yi.setDrawGridLines(true)
        yi.axisMinimum = 0f
        yi.axisMaximum = 1023f

        chartViewSoilHistory.axisRight.isEnabled = false
    }
}



