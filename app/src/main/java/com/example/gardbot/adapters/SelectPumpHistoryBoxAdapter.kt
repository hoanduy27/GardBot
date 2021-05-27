package com.example.gardbot.adapters

import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.example.gardbot.R

data class SelectPumpHistoryBoxAdapter (val boxList: ArrayList<SelectPumpHistoryBox>, val activity : FragmentActivity?) : BaseAdapter(){
    override fun getItem(position: Int): SelectPumpHistoryBox {
        return boxList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return boxList.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = View.inflate(activity, R.layout.layout_history_selection, null)
        val sensorText = view.findViewById<TextView>(R.id.history_Sensor)
            sensorText.text = boxList[position]._sensorName
        val pumpText = view.findViewById<TextView>(R.id.history_pump)
            pumpText.text = boxList[position].pumpName
        return view
    }
}
