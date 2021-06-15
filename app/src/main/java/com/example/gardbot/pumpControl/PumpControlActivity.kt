package com.example.gardbot.pumpControl

import android.R.attr.button
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gardbot.R
import com.example.gardbot.RetrofitClient
import com.example.gardbot.adapters.*
import com.example.gardbot.dashboard.SystemActivity
import com.example.gardbot.databinding.ActivityControlpumpBinding
import com.example.gardbot.history.HistorySelectPumpActivity
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class PumpControlActivity : AppCompatActivity() {
    private lateinit var binding: ActivityControlpumpBinding
    private lateinit var sysID: String
    private var pumpControlList = ArrayList<PumpControlBox>()
    private lateinit var adapter : PumpControlBoxAdapter
    private val database = Firebase.database

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_controlpump)
        //Load intent

        sysID = intent.getStringExtra("sysID")!!
        binding = ActivityControlpumpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //Do data
        var retrofitClient = RetrofitClient()
        retrofitClient.getClient(retrofitClient.getLocalHostUrl())

        adapter = PumpControlBoxAdapter(pumpControlList, this)
        binding.pCL.adapter= adapter

        //Fill adapter
        addPumpControlList()

        binding.agreeButton.setOnClickListener(View.OnClickListener {
            val pRef = database.reference
            var position=0;

            while (position < pumpControlList.size)
            {
                var tempValue = adapter.getItem(position)
                var key = tempValue.pumpId
                pRef.child("pump").child(key).child("auto").setValue(tempValue.auto)
                if (tempValue.auto == "0")
                {
                    retrofitClient.uploadValue(tempValue.pumpId, tempValue.waterLevel)
                }
                position++;
            }
            intent = Intent(this, HistorySelectPumpActivity::class.java)
            intent.putExtra("sysID", sysID)
            startActivity(intent)
        })
    }

    private fun addPumpControlList(){
        val mRef = database.reference
        mRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val pumpSnapshot = snapshot.child("pump")
                val soilSnapshot = snapshot.child("sensor/soilMoisture")
                for(pump in pumpSnapshot.children) {
                    val pumpID=pump.key.toString();
                    val soilID = pump.child("soilMoistureID").value.toString()
                    if(soilSnapshot.child(soilID).child("sysID").value.toString() == sysID){
                        val pumpName = pump.child("name").value.toString()
                        val auto = pump.child("auto").value.toString()
                        val waterLevel=pump.child("waterLevel").value.toString()
                        val sensorName = soilSnapshot.child(soilID).child("name").value.toString()
                        val moisture = soilSnapshot.child(soilID).child("moisture").value.toString()
                        pumpControlList.add(PumpControlBox(pumpName, sensorName, moisture,auto,waterLevel,pumpID))
                        adapter.notifyDataSetChanged()
                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}
