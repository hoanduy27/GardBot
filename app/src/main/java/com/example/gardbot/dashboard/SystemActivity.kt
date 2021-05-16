package com.example.gardbot.dashboard

import com.example.gardbot.adapters.Selection
import com.example.gardbot.adapters.SelectionAdapter
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.example.gardbot.ActivityPump
import com.example.gardbot.auth.AuthActivity
import com.example.gardbot.R
import com.example.gardbot.viewInfomation.ViewInfomationActivity
import com.example.gardbot.databinding.ActivitySystemBinding
import com.example.gardbot.history.HistorySelectPumpActivity
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.example.gardbot.model.Session

class SystemActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySystemBinding
    private var funcList = ArrayList<Selection>()
    private lateinit var adapter : SelectionAdapter
    private val database = Firebase.database
    private lateinit var sysID : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_system)

        sysID = intent.getStringExtra("sysID")!!
        binding = ActivitySystemBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val mRef = database.getReference().child("wateringSystem")
        //List func
        adapter = SelectionAdapter(funcList, this)
        binding.funcList.adapter = adapter

        addBasicFunc()
        adapter.notifyDataSetChanged()
        addOperatorManagerFunction(mRef)
        setupToggler(mRef)

        //Event handlers
        binding.autoToggler.setOnCheckedChangeListener { buttonView, isChecked ->
            mRef.child(sysID!!).child("autoStatus").setValue(when{
                isChecked -> "1"
                else -> "0"
            })
        }
        binding.funcList.setOnItemClickListener { parent, view : View, position, id : Long->
            if(id == 0L){
                intent = Intent(this, ViewInfomationActivity::class.java)
                startActivity(intent)
            }
            else if(id == 1L) {
                intent = Intent(this, ActivityPump::class.java)
                startActivity(intent)
            }

            else if(id == 2L){
                intent = Intent(this, HistorySelectPumpActivity::class.java)
                intent.putExtra("sysID", sysID)
                startActivity(intent)
            }
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

    fun addBasicFunc(){
        funcList.add(Selection("", "Xem thông tin", ""))
        funcList.add(Selection("", "Điều khiển máy bơm", ""))
        funcList.add(Selection("", "Lịch sử", ""))
        funcList.add(Selection("", "Dieu khien nguoi dung", ""))
    }
    fun addOperatorManagerFunction(mRef : DatabaseReference){

        //var sysID = intent.getStringExtra("sysID")
        mRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                binding.sysName.text = snapshot.child(sysID).child("name").value.toString()
                var omf = binding.funcList.getChildAt(3)
                if(snapshot.child(sysID!!).child("operator").child(Session.username).value.toString() == "0") {
                    omf.findViewById<TextView>(R.id.body).setBackgroundColor(resources.getColor(R.color.disabled))
                    omf.isClickable = false
                }
                else{
                    omf.findViewById<TextView>(R.id.body).setBackgroundColor(resources.getColor(R.color.black))
                    omf.isClickable = true
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    fun setupToggler(mRef : DatabaseReference){
        //var sysID = intent.getStringExtra("sysID")
        mRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                binding.autoToggler.isChecked = snapshot.child(sysID!!).child("autoStatus").value.toString() == "1"

            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }


}