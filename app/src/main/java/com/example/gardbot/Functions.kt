package com.example.gardbot

import Adapters.Selection
import Adapters.SelectionAdapter
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
import com.example.gardbot.databinding.FragmentFunctionsBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import model.Session
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage
import java.nio.charset.Charset
/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class Functions : Fragment() {

    private var _binding: FragmentFunctionsBinding? = null
    private lateinit var systemAdapter : SelectionAdapter
    private lateinit var database: DatabaseReference

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFunctionsBinding.inflate(inflater, container, false)
        database = FirebaseDatabase.getInstance().getReference("wateringSystem")
        //Add to list

        systemAdapter = SelectionAdapter(addFunctions(), activity)
        //Fill gridview
        binding.funcList.adapter = systemAdapter
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.funcList.setOnItemClickListener { parent, view, position, id ->
            var systemName = arguments?.getString("systemName")
            var systemID = arguments?.getString("systemID")
            var args = Bundle()
            args.putString("systemName", systemName)
            args.putString("systemID", systemID)
            if(id == 2L){

                findNavController().navigate(R.id.historySelectPump, args)
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    fun addFunctions() : ArrayList<Selection>{
        //TODO: Read from firebase
        var funcList : ArrayList<Selection> = ArrayList()
        funcList.add(Selection("", "Xem thông tin", ""))
        funcList.add(Selection("", "Điều khiển máy bơm", ""))
        funcList.add(Selection("", "Lịch sử", ""))
        funcList.add(Selection("", "Quản lý người dùng", ""))
        return funcList
    }
    companion object{
        fun newInstance(): Dashboard = Dashboard()

    }
}