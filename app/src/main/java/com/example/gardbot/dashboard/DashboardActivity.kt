package com.example.gardbot.dashboard

import com.example.gardbot.adapters.Selection
import com.example.gardbot.adapters.SelectionAdapter
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
import androidx.navigation.ui.AppBarConfiguration
import com.example.gardbot.auth.AuthActivity
import com.example.gardbot.R
import com.example.gardbot.databinding.ActivityDashboardBinding
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.example.gardbot.model.Session

class DashboardActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityDashboardBinding
    private var sysList = ArrayList<Selection>()
    private lateinit var systemAdapter : SelectionAdapter
    private val database = Firebase.database

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //List system
        systemAdapter = SelectionAdapter(sysList, this)
        binding.sysList.adapter = systemAdapter

        //Add systems
        addSystem()

        //Event handlers
        binding.sysList.setOnItemClickListener { parent, view : View, position, id : Long->
            Log.e("grid id", id.toString())
            val intent = Intent(this, SystemActivity::class.java)
            intent.putExtra("sysID", view.findViewById<TextView>(R.id.footer).text.toString())
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

    fun addSystem(){
        val mRef = database.getReference().child("wateringSystem")
        mRef.addChildEventListener(object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                if(snapshot.child("operator").hasChild(Session.username)){
                    var sysName = snapshot.child("name").value.toString()
                    var sysID = snapshot.key.toString()
                    var isOwner = snapshot.child("operator").child(Session.username).value.toString()
                    var ownership = if(isOwner == "1"){
                        "Chủ sở hữu"
                    }
                    else{
                        "Người điều khiển"
                    }
                    sysList.add(Selection(ownership, sysName, sysID))
                    systemAdapter.notifyDataSetChanged()
                }
            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                sysList.clear()
                mRef.removeEventListener(this)
                mRef.addChildEventListener(this)
                systemAdapter.notifyDataSetChanged()
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                sysList.clear()
                mRef.removeEventListener(this)
                mRef.addChildEventListener(this)
                systemAdapter.notifyDataSetChanged()
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}