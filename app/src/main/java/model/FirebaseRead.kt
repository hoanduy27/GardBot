package model

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.provider.ContactsContract
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import com.example.gardbot.MainActivity
import com.google.firebase.database.*



class FirebaseRead {
    private lateinit var database: DatabaseReference

    companion object{
        fun mReadDataOnce(child : String, listener: OnGetDataListener){
            listener.onStart()
            FirebaseDatabase.getInstance().getReference(child).addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    listener.onSuccess(snapshot)
                }

                override fun onCancelled(error: DatabaseError) {
                    listener.onFailed(error)
                }
            })
        }

        fun getWateringSystem(id : String, context: Context, ws:WateringSystem) {
            var progressBar = ProgressBar(context)
            progressBar.visibility = View.VISIBLE

            var database = FirebaseDatabase.getInstance().getReference("wateringSystem")
            database.addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.hasChild(id)){
                        ws.autoStatus = snapshot.child("autoStatus").value.toString()
                        ws.name = snapshot.child("name").value.toString()
                        for(op in snapshot.child("operator").children){
                            ws.operator.add(op.value.toString())
                        }
                    }
                    if(progressBar.visibility == View.VISIBLE){
                        progressBar.visibility = View.GONE
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
        }
    }
}