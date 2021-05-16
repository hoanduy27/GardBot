package com.example.gardbot

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
import com.example.gardbot.databinding.FragmentDashboardBinding
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
class Dashboard : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    private lateinit var systemAdapter : SelectionAdapter
    private lateinit var database: DatabaseReference



    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        database = FirebaseDatabase.getInstance().getReference("wateringSystem")
        //Add to list
        systemAdapter = SelectionAdapter(addSystem(), activity)
        //Fill gridview
        binding.sysList.adapter = systemAdapter
        //Change background to alert background: red
        //alertSystem()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.sysList.setOnItemClickListener { parent, view : View, position, id : Long->
            Log.e("grid id", id.toString())

            if(id == (binding.sysList.size - 1).toLong()){

                Session.mqtt.sendDataMqtt("0")
                //Test MQTT
            }
            else{
                var systemName = view.findViewById<TextView>(R.id.body).text.toString()
                var systemID = view.findViewById<TextView>(R.id.footer).text.toString()

                var args = Bundle()
                args.putString("systemName", systemName)
                args.putString("systemID", systemID)
                findNavController().navigate(R.id.functions, args)
            }

        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    fun addSystem() : ArrayList<Selection>{

        /*var mProgressBar = ProgressBar(this.context);
        mProgressBar.visibility = View.VISIBLE;
        var sysList : ArrayList<Selection> = ArrayList()
        database.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                //Admin
                for(system in snapshot.children){
                    var operators = system.child("operator")
                    Log.e("ops", operators.toString())
                    if(operators.hasChild(Session.username) && operators.child(Session.username).value.toString() == "1"){
                        var header = "Chủ sở hữu"
                        var body = system.child("name").value.toString()
                        var footer = system.key.toString()
                        sysList.add(Selection(header, body, footer))
                    }
                }
                for(system in snapshot.children){
                    var operators = system.child("operator")
                    if(operators.hasChild(Session.username) && operators.child(Session.username).value.toString() == "0"){
                        var header = "Người điều khiển"
                        var body = system.child("name").value.toString()
                        var footer = system.key.toString()
                        sysList.add(Selection(header, body, footer))
                    }
                }
                if(mProgressBar.visibility == View.VISIBLE){
                    mProgressBar.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })*/

        var sysList = ArrayList<Selection>()
        sysList.add(Selection("Owner", "HT1", "GB0001"))
        sysList.add(Selection("Owner", "HT2", "GB0002"))
        sysList.add(Selection("Demo pump", "Demo", "Demo"))
        return sysList
    }
    fun alertSystem(){
        //TODO (not yet completed): Change background to red
        binding.sysList.adapter.getView(1, null, binding.sysList).setBackgroundResource(R.color.alert)
    }
    companion object{
        fun newInstance(): Dashboard = Dashboard()
    }

}