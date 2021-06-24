package com.example.gardbot.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.gardbot.model.SoilHistory
import com.example.gardbot.R

class CustomSoilDetailAdapter (var ctx: Context, var resource : Int, var Items: ArrayList<SoilHistory>, var unit : String) : ArrayAdapter<SoilHistory>(ctx, resource, Items) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater : LayoutInflater = LayoutInflater.from(ctx)
        val view = layoutInflater.inflate(resource, null)

        val leftPart = view.findViewById<TextView>(R.id.leftPart)
        val rightPart = view.findViewById<TextView>(R.id.rightPart)

        // set item data
        leftPart.text = Items[position].time
        rightPart.text = "${Items[position].value} ${this.unit}"


        if (Items[position]!!.value!!.toInt()  < 10){
            rightPart.setTextColor(Color.parseColor("#db5a6b"))
            leftPart.setTextColor(Color.parseColor("#db5a6b"))
        }

        return view
    }
}