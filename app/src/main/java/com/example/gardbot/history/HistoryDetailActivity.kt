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
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.example.gardbot.auth.AuthActivity
import com.example.gardbot.R
import com.example.gardbot.databinding.ActivityHistoryDetailBinding
import com.example.gardbot.model.Pump
import com.example.gardbot.utils.DateFormatUtils
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class HistoryDetailActivity: AppCompatActivity() {
    private lateinit var binding: ActivityHistoryDetailBinding
    //intent data
    private lateinit var timestamp: String
    private lateinit var pumpId: String
    private lateinit var sensorName: String
    //Detail data

    private val database = Firebase.database

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_detail)

        binding = ActivityHistoryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Load intent data
        timestamp = intent.getStringExtra("timestamp")!!
        pumpId = intent.getStringExtra("pumpId")!!
        sensorName = intent.getStringExtra("sensorName")!!
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
        binding.historyInside.text = DateFormatUtils.datetimeFormat.format(timestamp.toLong() * 1000)

    }
    fun addDetail(){
        val mRef = database.reference.child("history/watering").child(pumpId).child(timestamp)
        mRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val autoStart = if(snapshot.child("autoStart").value.toString() == "1") {1} else {0}
                val autoEnd = if(snapshot.child("autoEnd").value.toString() == "1") {1} else {0}

                val autoStart_ic = if(autoStart == 1){R.drawable.ic_auto} else {0}
                val autoEnd_ic = if(autoEnd == 1){R.drawable.ic_auto} else {0}

                val auto_ic = if(autoStart + autoEnd > 0){R.drawable.ic_auto} else {0}

                binding.historyInside.setCompoundDrawablesWithIntrinsicBounds(0, 0, auto_ic, 0)

                binding.pumpInf.text = sensorName;
                binding.timeStart.text =
                    DateFormatUtils.timeFormat.format(snapshot.child("startTime").value.toString().toLong()*1000)
                binding.timeStart.setCompoundDrawablesWithIntrinsicBounds(0,0,autoStart_ic,0)

                binding.timeEnd.text  =
                    DateFormatUtils.timeFormat.format(snapshot.child("endTime").value.toString().toLong()*1000)
                binding.timeEnd.setCompoundDrawablesWithIntrinsicBounds(0,0,autoEnd_ic,0)

                binding.airMoistureBegin.text = snapshot.child("humidityStart").value.toString();
                binding.airMoistureEnd.text = snapshot.child("humidityEnd").value.toString();
                binding.soilMoistureBegin.text = snapshot.child("moistureStart").value.toString();
                binding.soilMoistureEnd.text = snapshot.child("moistureEnd").value.toString();
                binding.tempBegin.text = snapshot.child("temperatureStart").value.toString();
                binding.tempEnd.text = snapshot.child("temperatureEnd").value.toString();
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}