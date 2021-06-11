package com.example.gardbot.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.gardbot.Model.Pump
import com.example.gardbot.model.SoilSensor
import com.example.gardbot.R

class CustomPumpInfoAdapter(var ctx: Context, var resource : Int, var Items: ArrayList<Pump>) : ArrayAdapter<Pump>(ctx, resource, Items) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater : LayoutInflater = LayoutInflater.from(ctx)
        val view = layoutInflater.inflate(resource, null)

        val sensorID = view.findViewById<TextView>(R.id.textViewSensorID)
        val pumpID = view.findViewById<TextView>(R.id.textViewPumpID)
        val pumpStatus = view.findViewById<TextView>(R.id.textViewPumpStatus)

        // set item data
        sensorID.text = Items[position]!!.soilMoistureID
        pumpID.text = Items[position]!!.name
        if (Items[position]!!.waterLevel == "0")
            pumpStatus.text = "OFF"
        else
            pumpStatus.text = "ON"

        return view
    }
}