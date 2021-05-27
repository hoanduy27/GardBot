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
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.example.gardbot.auth.AuthActivity
import com.example.gardbot.R
import com.example.gardbot.databinding.ActivityHistoryDetailBinding
import com.example.gardbot.model.Pump
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class HistoryDetailActivity: AppCompatActivity() {
    private lateinit var binding: ActivityHistoryDetailBinding
    //intent data
    private lateinit var hisLine: String
    private lateinit var pumpId: String
    private lateinit var sensorId: String
    //Detail data

    private val database = Firebase.database

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_detail)

        binding = ActivityHistoryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Load intent data
        hisLine = intent.getStringExtra("hisLine")!!
        pumpId = intent.getStringExtra("pumpId")!!
        sensorId = intent.getStringExtra("sensorId")!!
        //Detail


        //Add history
        addHistory()
        addDetail()
        //Event handlers

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

    fun addHistory(){
        binding.historyInside.text = hisLine
    }
    fun addDetail(){
        val mRef = database.reference.child("history/watering").child(pumpId)
        mRef.addChildEventListener(object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                binding.pumpInf.text = "Sensor: " + sensorId;
                binding.timeStart.text = snapshot.child("startTime").getValue().toString();
                binding.timeEnd.text  = snapshot.child("endtime").getValue().toString();
                binding.airMoistureBegin.text = snapshot.child("humidityStart").getValue().toString();
                binding.airMoistureEnd.text = snapshot.child("humidityEnd").getValue().toString();
                binding.soilMoistureBegin.text = snapshot.child("moistureStart").getValue().toString();
                binding.soilMoistureEnd.text = snapshot.child("moistureEnd").getValue().toString();
                binding.tempBegin.text = snapshot.child("temperatureStart").getValue().toString();
                binding.tempEnd.text = snapshot.child("temperatureEnd").getValue().toString();
            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}