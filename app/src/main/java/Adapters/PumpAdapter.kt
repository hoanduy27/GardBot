package Adapters

import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.example.gardbot.R

data class PumpAdapter(val pumpList: List<Pump>, val activity: FragmentActivity?): BaseAdapter() {
    override fun getItem(position: Int): Any {
        return pumpList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return pumpList.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = View.inflate(activity, R.layout.layout_pump, null)
        val pumpName = view.findViewById<TextView>(R.id.pumpName)
        val pumpState = view.findViewById<TextView>(R.id.pumpState)

        pumpName.text = pumpList[position].pumpName
        pumpState.text = pumpList[position].pumpState

        return view
    }
}
