package com.example.gardbot.adapters

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.FragmentActivity
import com.example.gardbot.R
import com.example.gardbot.RetrofitClient

data class PumpControlBoxAdapter (val boxList: ArrayList<PumpControlBox>, val activity : FragmentActivity?) : BaseAdapter(){


    override fun getItem(position: Int): PumpControlBox {
        return boxList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return boxList.size
    }

    override fun isEnabled(position: Int): Boolean {
        return false
    }

    fun isNotEnabled(position: Int): Boolean {
        return true
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = View.inflate(activity, R.layout.singlerow, null)
        val sensorText = view.findViewById<TextView>(R.id.sensor)
        sensorText.text = boxList[position].sensorName

        val pumpText = view.findViewById<CheckBox>(R.id.pump)
        pumpText.text = boxList[position].pumpName

        if (boxList[position].waterLevel == "1")
        {
            pumpText.isChecked=true
        }

        val pumpID= view.findViewById<TextView>(R.id.pumpid)
        pumpID.text = boxList[position].pumpId;
        val moistureText = view.findViewById<TextView>(R.id.moisture)
        moistureText.text = boxList[position].moisture

        val autoState=view.findViewById<SwitchCompat>(R.id.switchstate)

        val temp=position

        if (boxList[position].auto== "1" )
        {
            autoState.isChecked=true;
            pumpText.isClickable=isEnabled(position);
        }

        autoState.setOnClickListener(View.OnClickListener {
            val onepump=view.findViewById<LinearLayout>(R.id.onepump)
            if (autoState.isChecked) {
                pumpText.isClickable=isEnabled(position);
                pumpText.isChecked = false;
                boxList[position].auto="1";
                boxList[position].waterLevel="0";
            } else {
                onepump.isClickable=true;
                pumpText.isChecked=false;
                boxList[position].waterLevel="0";
                pumpText.isClickable=isNotEnabled(position);
                boxList[position].auto="0";
            }
        })

        pumpText.setOnClickListener(View.OnClickListener {
            val linear=view.findViewById<LinearLayout>(R.id.info)
            if (autoState.isChecked)
            {
                pumpText.isChecked = false;
                pumpText.isClickable=false;
            }
            if (pumpText.isChecked && !autoState.isChecked) {
                boxList[position].waterLevel="1";
            } else if (!pumpText.isChecked && !autoState.isChecked)   {
                boxList[position].waterLevel="0";
            }
        })

        return view
    }
}