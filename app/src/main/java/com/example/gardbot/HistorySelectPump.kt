package com.example.gardbot

import Adapters.Box
import Adapters.BoxAdapter
import Adapters.Selection
import Adapters.SelectionAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.get
import androidx.core.view.size
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.gardbot.databinding.FragmentHistorySelectPumpBinding
import com.google.firebase.database.*
import model.FirebaseRead
import model.Session
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage
import java.nio.charset.Charset




/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class HistorySelectPump : Fragment() {

    private var _binding: FragmentHistorySelectPumpBinding? = null

    private lateinit var pumpAdapter : BoxAdapter
    private lateinit var database: DatabaseReference
    private var pumpList : ArrayList<Box> = ArrayList()



    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHistorySelectPumpBinding.inflate(inflater, container, false)
        //database = FirebaseDatabase.getInstance().getReference("wateringSystem")
        //Add to list
        addPump()
        pumpAdapter = BoxAdapter(pumpList, activity)
        //Fill gridview
        binding.pumpList.adapter = pumpAdapter

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.pumpList.setOnItemClickListener { parent, view : View, position, id : Long->
            var systemName = arguments?.getString("systemName")
            var systemID = arguments?.getString("systemID")
            var args = Bundle()
            args.putString("systemName", systemName)
            args.putString("systemID", systemID)
            args.putString("pumpID", pumpList[id.toInt()].id.toString())
            args.putString("pumpName", pumpList[id.toInt()].text.toString())
            findNavController().navigate(R.id.historyPump, args)
        }

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    fun addPump() {
        pumpList.add(Box("p0001", "Máy bơm P1 - Sensor S1" ))
        pumpList.add(Box("p0002", "Máy bơm P2 - Sensor S1" ))
        pumpList.add(Box("p0003", "Máy bơm P3 - Sensor S2" ))
        pumpList.add(Box("p0004", "Máy bơm P4 - Sensor S3" ))
    }
}