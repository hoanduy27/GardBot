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
import com.example.gardbot.databinding.FragmentHistoryPumpBinding
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
class HistoryPump : Fragment() {

    private var _binding: FragmentHistoryPumpBinding? = null

    private lateinit var pumpAdapter : BoxAdapter
    private lateinit var database: DatabaseReference
    private var historyList : ArrayList<Box> = ArrayList()



    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHistoryPumpBinding.inflate(inflater, container, false)
        //database = FirebaseDatabase.getInstance().getReference("wateringSystem")
        //Add to list
        addHistory()
        pumpAdapter = BoxAdapter(historyList, activity)
        //Fill gridview
        binding.pumpHistory.adapter = pumpAdapter

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*binding.pumpHistory.setOnItemClickListener { parent, view : View, position, id : Long->
            var systemName = arguments?.getString("systemName")
            var systemID = arguments?.getString("systemID")
            var pumpID = arguments?.getString("pumpID")
            var pumpName = arguments?.getString("pumpName")
            var args = Bundle()
            args.putString("systemName", systemName)
            args.putString("systemID", systemID)
            args.putString("pumpID", pumpID)
            args.putString("pumpName", pumpName)
            findNavController().navigate(R.id.historyPump, args)
        }*/

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    fun addHistory() {

        historyList.add(Box("09-05-2021-12:00:00", "09-05-2021 12:00:00" ))
        historyList.add(Box("09-05-2021-12:30:00", "09-05-2021 12:30:00" ))
        historyList.add(Box("09-05-2021-13:00:00", "09-05-2021 13:00:00" ))
        historyList.add(Box("09-05-2021-14:00:00", "09-05-2021 14:00:00" ))
    }
}