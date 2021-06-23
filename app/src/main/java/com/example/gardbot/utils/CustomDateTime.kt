package com.example.gardbot.utils

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.ValueFormatter

class CustomDateTime(var startTime : Long) : ValueFormatter() {
    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        return DateFormatUtils.timeFormat.format((startTime + value.toLong())*1000)
    }
}