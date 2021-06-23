package com.example.gardbot.utils

import android.content.Context
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import androidx.core.content.ContextCompat
import com.example.gardbot.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

class TimeseriesUtils(var lineChart : LineChart) {
    companion object
    {
        fun drawChart(chart : LineChart, context : Context, offsets : ArrayList<Entry>, startTime : Long, yAxisMin : Float, yAxisMax : Float){
            var gradient = LinearGradient(0f, 0f, 0f, 600f, Color.GREEN, Color.WHITE, Shader.TileMode.CLAMP)
            chart.setTouchEnabled(true)
            chart.isDragEnabled = true
            chart.setScaleEnabled(true)
            chart.setDrawGridBackground(false)
            chart.setPinchZoom(false)
            chart.description.isEnabled = false
            chart.renderer.paintRender.shader = gradient
            // Set data for plotting
            val dataset = LineDataSet(offsets, null)
            dataset.fillDrawable = ContextCompat.getDrawable(context, R.drawable.green_gradient)
            dataset.setDrawFilled(true)

            val data = LineData(dataset)
            data.setValueTextColor(Color.BLACK)
            chart.data = data

            // Format legend, xAxis, yAxis
            chart.legend.isEnabled = false

            val xi = chart.xAxis
            xi.textColor = Color.BLACK
            xi.labelCount = 5
            xi.position = XAxis.XAxisPosition.BOTTOM
            if(offsets.size > 0){
                xi.valueFormatter = CustomDateTime(startTime)
            }

            val yi = chart.axisLeft
            yi.textColor = Color.BLACK
            yi.setDrawGridLines(true)
            yi.axisMinimum = yAxisMin
            yi.axisMaximum = yAxisMax

            chart.axisRight.isEnabled = false
        }
    }
}