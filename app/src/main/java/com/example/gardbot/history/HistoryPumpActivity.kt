package com.example.gardbot.history

import com.example.gardbot.adapters.Box
import com.example.gardbot.adapters.BoxAdapter
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.example.gardbot.auth.AuthActivity
import com.example.gardbot.R
import com.example.gardbot.databinding.ActivityHistoryPumpBinding
import com.example.gardbot.model.Pump
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class HistoryPumpActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHistoryPumpBinding

    private var historyList = ArrayList<Box>()

    private lateinit var adapter : BoxAdapter
    //intent data
    private lateinit var sysID : String
    private lateinit var pumpId : String
    private lateinit var pump : Pump

    private val database = Firebase.database

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_pump)

        binding = ActivityHistoryPumpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Load intent data
        sysID = intent.getStringExtra("sysID")!!
        pumpId = intent.getStringExtra("pumpId")!!
        pump = intent.getSerializableExtra("pump") as Pump


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
                    .setTitle("Đăng xuất")
                    .setMessage("Bạn có muốn đăng xuất không?")
                    .setPositiveButton("Có") { dialogInterface: DialogInterface, i: Int ->
                        val intent = Intent(this, AuthActivity::class.java)
                        startActivity(intent)
                    }
                    .setNegativeButton("Không"){ dialogInterface: DialogInterface, i:Int->}
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
        var pumpName = "Máy bơm ${pump.name}"
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
                historyList.add(Box(snapshot.key.toString()))
                adapter.notifyDataSetChanged()
            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                historyList.clear()
                mRef.removeEventListener(this)
                mRef.addChildEventListener(this)
                adapter.notifyDataSetChanged()
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                historyList.clear()
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
