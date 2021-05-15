package Adapters

import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.GridView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.example.gardbot.R

data class SensorAdapter(val sensor: List<Sensor>, val activity: FragmentActivity?): BaseAdapter() {
    override fun getItem(position: Int): Any {
        return sensor[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return sensor.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = View.inflate(activity, R.layout.layout_sensor, null)
        val sensorName = view.findViewById<TextView>(R.id.sensorName)
        val pumpList= view.findViewById<GridView>(R.id.pumpList)

        sensorName.text = sensor[position].sensorName

        var pumpAdapter = PumpAdapter(sensor[position].pumpList, activity)
        pumpList.adapter = pumpAdapter

        return view
    }
}
