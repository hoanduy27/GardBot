package com.example.gardbot.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.gardbot.model.SoilSensor
import com.example.gardbot.R

class CustomCurSoilAdapter(var ctx:Context, var resource : Int, var Items: ArrayList<SoilSensor>) : ArrayAdapter<SoilSensor>(ctx, resource, Items){
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater : LayoutInflater = LayoutInflater.from(ctx)
        val view = layoutInflater.inflate(resource, null)

        val leftPart = view.findViewById<TextView>(R.id.leftPart)
        val rightPart = view.findViewById<TextView>(R.id.rightPart)

        // set item data
        leftPart.text = Items[position].name?.substringBefore(' ')
        rightPart.text = Items[position].moisture + "%"


        if (Items[position]!!.moisture!!.toInt()  < 10)
            view.setBackgroundResource(R.drawable.custom_info_warning)

        return view
    }
}