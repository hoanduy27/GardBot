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
import com.example.gardbot.databinding.FragmentDashboardBinding
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
class Dashboard : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private var sysList : ArrayList<Selection> = ArrayList()
    private lateinit var systemAdapter : SelectionAdapter
    private lateinit var database: DatabaseReference

    private lateinit var mqttService : MqttService

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
        addSystem()
        systemAdapter = SelectionAdapter(sysList, activity)
        //Fill gridview
        binding.sysList.adapter = systemAdapter
        //Change background to alert background: red
        alertSystem()

        //MQTT
        //mqttService = MqttService(this)
        mqttService = MqttService(this.context)

        mqttService.setCallback(object : MqttCallbackExtended {
            override fun connectComplete(reconnect: Boolean, serverURI: String?) {

            }

            override fun connectionLost(cause: Throwable?) {

            }

            override fun messageArrived(topic: String?, message: MqttMessage?) {
                //val data_to_microbit = message.toString()
                Log.d(topic, message.toString())
            }

            override fun deliveryComplete(token: IMqttDeliveryToken?) {

            }
        })
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.sysList.setOnItemClickListener { parent, view : View, position, id : Long->
            if(id == 2L){
                //Test MQTT
                sendDataMqtt("0")
                //findNavController().navigate(R.id.sensorPumpFragment)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    fun addSystem(){
        //TODO: Read from firebase
        sysList.add(Selection("Owner", "HT1", "GB0001"))
        sysList.add(Selection("Owner", "HT2", "GB0002"))
        sysList.add(Selection("Demo pump", "Demo", "Demo"))

    }
    fun alertSystem(){
        //TODO (not yet completed): Change background to red
        binding.sysList.adapter.getView(1, null, binding.sysList).setBackgroundResource(R.color.alert)
    }
    companion object{
        fun newInstance(): Dashboard = Dashboard()

    }

    fun sendDataMqtt(data : String){
        val msg = MqttMessage()
        msg.id = 1234
        msg.qos = 0
        msg.isRetained = true
        val b : ByteArray = data.toByteArray(Charset.forName("UTF-8"))
        msg.payload = b
        Log.d("MQTT", "Publish: $msg")
        try{
            mqttService.mqttAndroidClient.publish(mqttService.subscriptionTopic, msg);
        }catch (e: MqttException){
            Log.d("MQTT", "sendDataMQTT: cannot send msg")
        }

    }
}