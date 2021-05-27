package com.example.gardbot.adapters

import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.example.gardbot.R

data class BoxAdapter (val boxList: ArrayList<Box>, val activity : FragmentActivity?) : BaseAdapter(){
    override fun getItem(position: Int): Box {
        return boxList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return boxList.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = View.inflate(activity, R.layout.inside_history_selection, null)
        val textView = view.findViewById<TextView>(R.id.inside_history_selection)
        textView.text = boxList[position].text
        return view
    }
}
