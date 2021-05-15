package com.example.gardbot

import Adapters.*
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.view.get
import androidx.navigation.fragment.findNavController
import com.example.gardbot.databinding.FragmentSensorpumpBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import model.Session

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SensorPumpFragment : Fragment() {

    private var _binding: FragmentSensorpumpBinding? = null
    private var sensorList : ArrayList<Sensor> = ArrayList()
    private lateinit var sensorAdapter: SensorAdapter
    private lateinit var database: DatabaseReference

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSensorpumpBinding.inflate(inflater, container, false)
        database = FirebaseDatabase.getInstance().getReference("wateringSystem")
        //Add to list
        addSensor()
        sensorAdapter = SensorAdapter(sensorList, activity)
        //Fill gridview
        binding.sensorList.adapter = sensorAdapter
        Log.e("e", sensorList.toString())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    fun addSensor(){
        //TODO: Read from firebase
        sensorList.add(Sensor("Sensor 1", addPump("sensor 1")));
        sensorList.add(Sensor("Sensor 2", addPump("sensor 2")));
        Log.e("e", sensorList[0].pumpList.toString())


    }
    fun addPump(sensorId: String) : ArrayList<Pump>{
        var pumpList : ArrayList<Pump> = ArrayList()
        pumpList.add(Pump("Pump 1", "ON"))
        pumpList.add(Pump("Pump 2", "OFF"))
        pumpList.add(Pump("Pump 3", "OFF"))
        return pumpList
    }
   /* fun alertSystem(){
        binding.sysList.adapter.getView(1, null, binding.sysList).setBackgroundResource(R.color.alert)
    }*/
    companion object{
        fun newInstance(): Dashboard = Dashboard()
    }
}