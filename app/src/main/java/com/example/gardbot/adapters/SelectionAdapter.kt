package com.example.gardbot.adapters

import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.example.gardbot.R

data class SelectionAdapter(val selection: List<Selection>, val activity: FragmentActivity?): BaseAdapter(){
    override fun getItem(position: Int): Any {
        return selection[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return selection.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = View.inflate(activity, R.layout.layout_selection, null)
        val header = view.findViewById<TextView>(R.id.header)
        val body = view.findViewById<TextView>(R.id.body)
        val footer = view.findViewById<TextView>(R.id.footer)

        header.text = selection[position].header
        body.text = selection[position].body
        body.setCompoundDrawablesWithIntrinsicBounds(0,selection[position].drawable,0,0)
        footer.text = selection[position].footer

        return view
    }
}
