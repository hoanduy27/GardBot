package com.example.gardbot.history

import com.example.gardbot.adapters.Box
import com.example.gardbot.adapters.BoxAdapter
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.get
import com.example.gardbot.auth.AuthActivity
import com.example.gardbot.R
import com.example.gardbot.databinding.ActivityHistoryPumpBinding
import com.example.gardbot.model.Pump
import com.example.gardbot.utils.DateFormatUtils
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class HistoryPumpActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHistoryPumpBinding

    private var historyList = ArrayList<Box>()
    private var timestamps = ArrayList<String>()

    private lateinit var adapter : BoxAdapter
    //intent data
    private lateinit var pumpId : String
    private lateinit var pump : Pump
    private  lateinit var sensorName: String
    private val database = Firebase.database

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_pump)

        binding = ActivityHistoryPumpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Load intent data
        pumpId = intent.getStringExtra("pumpId")!!
        pump = intent.getSerializableExtra("pump") as Pump
        sensorName = intent.getStringExtra("sensorName")!!
        //Get intent data for later history


        //Create Adapter
        adapter = BoxAdapter(historyList, this)
        binding.pumpHistory.adapter= adapter

        //Set header: Pump and sensor
        setHeader()
        //Add history list
        addHistoryList()

        //Event handlers
        binding.pumpHistory.setOnItemClickListener { parent, view : View, position, id : Long->
            //
            var intent = Intent(this, HistoryDetailActivity::class.java)
            intent.putExtra("timestamp", timestamps[position])
            intent.putExtra("pumpId", pumpId)
            intent.putExtra("sensorName", sensorName)
            startActivity(intent)
        }
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.signout -> {
                AlertDialog.Builder(this)
                    .setTitle("????ng xu???t")
                    .setMessage("B???n c?? mu???n ????ng xu???t kh??ng?")
                    .setPositiveButton("C??") { dialogInterface: DialogInterface, i: Int ->
                        val intent = Intent(this, AuthActivity::class.java)
                        startActivity(intent)
                    }
                    .setNegativeButton("Kh??ng"){ dialogInterface: DialogInterface, i:Int->}
                    .show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    /*override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }*/

    fun setHeader(){
        var pumpName = "M??y b??m: ${pump.name}"
        binding.pumpHistoryPumpName.text =  pumpName
        val mRef = database.reference.child("sensor/soilMoisture")
        mRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var sensorName = "Sensor: ${snapshot.child(pump.soilMoistureID!!).child("name").value.toString()}"
                binding.pumpHistorySensorName.text = sensorName
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
    fun addHistoryList(){
        val mRef = database.reference.child("history/watering").child(pumpId)
        mRef.addChildEventListener(object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {

                val autoStart = if(snapshot.child("autoStart").value.toString() == "1"){1} else {0}
                val autoEnd = if(snapshot.child("autoEnd").value.toString() == "1"){1} else {0}

                val auto_ic= if(autoStart + autoEnd > 0){R.drawable.ic_auto} else {0}

                historyList.add(Box(
                    DateFormatUtils.datetimeFormat.format(snapshot.key!!.toLong()*1000), auto_ic))
                timestamps.add(snapshot.key!!)
                adapter.notifyDataSetChanged()
            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                historyList.clear()
                timestamps.clear()
                mRef.removeEventListener(this)
                mRef.addChildEventListener(this)
                adapter.notifyDataSetChanged()
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                historyList.clear()
                timestamps.clear()
                mRef.removeEventListener(this)
                mRef.addChildEventListener(this)
                adapter.notifyDataSetChanged()
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}

