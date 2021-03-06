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
import androidx.appcompat.app.AlertDialog
import androidx.navigation.ui.AppBarConfiguration
import com.example.gardbot.auth.AuthActivity
import com.example.gardbot.databinding.ActivityHistorySelectPumpBinding
import com.example.gardbot.R
import com.example.gardbot.adapters.SelectPumpHistoryBox
import com.example.gardbot.adapters.SelectPumpHistoryBoxAdapter
import com.example.gardbot.model.Pump
import com.example.gardbot.model.Session
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class HistorySelectPumpActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityHistorySelectPumpBinding
    private lateinit var sensorName: String

    private var pumpListView = ArrayList<SelectPumpHistoryBox>()
    private var pumpList = ArrayList<Pump>()
    private var pumpIds = ArrayList<String>()

    private lateinit var adapter : SelectPumpHistoryBoxAdapter
    private val database = Firebase.database

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_select_pump)

        binding = ActivityHistorySelectPumpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Create Adapter
        adapter = SelectPumpHistoryBoxAdapter(pumpListView, this)
        binding.pumpList.adapter= adapter

        //Add pumps
        addPump()

        //Event handlers

        binding.pumpList.setOnItemClickListener { parent, view : View, position, id : Long->
            var intent = Intent(this, HistoryPumpActivity::class.java)
            intent.putExtra("pumpId", pumpIds[id.toInt()])
            intent.putExtra("pump", pumpList[id.toInt()])
            sensorName = adapter.getItem(position).sensorName
            intent.putExtra("sensorName",sensorName)
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

    fun addPump(){
        val mRef = database.reference
        mRef.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                pumpIds.clear()
                pumpListView.clear()
                pumpList.clear()
                var pumpSnapshot = snapshot.child("pump")
                var soilSnapshot = snapshot.child("sensor/soilMoisture")
                for(pump in pumpSnapshot.children){
                    var soilID = pump.child("soilMoistureID").value.toString()
                    //If this pump is in this system
                    if(soilSnapshot.child(soilID).child("sysID").value == Session.sysID){
                        var p = pump.getValue(Pump::class.java)
                        pumpIds.add(pump.key.toString())
                        pumpList.add(p!!)
                        pumpListView.add(SelectPumpHistoryBox("M??y b??m: " + p.name!!, "Sensor: " + soilSnapshot.child(p.soilMoistureID!!).child("name").value.toString()))
                        adapter.notifyDataSetChanged()
                    }

                }
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}