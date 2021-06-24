package com.example.gardbot.ui.manager

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.renderscript.Sampler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.forEach
import com.example.gardbot.R
import com.example.gardbot.adapters.Operator
import com.example.gardbot.adapters.OperatorAdapter
import com.example.gardbot.auth.AuthActivity
import com.example.gardbot.dashboard.SystemActivity
import com.example.gardbot.databinding.ActivityOperatorManagerBinding
import com.example.gardbot.databinding.FragmentViewOperatorBinding
import com.example.gardbot.model.Session
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ViewOperatorFragment : Fragment() {
    private lateinit var _binding : FragmentViewOperatorBinding
    private var adminList = ArrayList<Operator>()
    private var opList = ArrayList<Operator>()
    private lateinit var adminAdapter : OperatorAdapter
    private lateinit var opAdapter : OperatorAdapter
    private val database = Firebase.database
    private val mRef = database.reference.child("wateringSystem/${Session.sysID}/operator")
    private lateinit var mRefListener : ValueEventListener
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //var view = inflater.inflate(R.layout.fragment_add_operator, container, false)
        _binding = FragmentViewOperatorBinding.inflate(inflater, container, false)

        // fill adapter
        adminAdapter = OperatorAdapter(adminList, activity)
        opAdapter = OperatorAdapter(opList, activity)
        binding.lstViewAdmin.adapter = adminAdapter
        binding.lstViewOperator.adapter = opAdapter
        addOps()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        binding.btnDelConfirm.setOnClickListener {
            removeOperator()
        }
        binding.btnDelCancel.setOnClickListener {
//            val intent = Intent(this.context, SystemActivity::class.java)
//            startActivity(intent)
            requireActivity().onBackPressed()
        }

    }
    private fun addOps(){
        mRefListener = object:ValueEventListener{
            override fun onDataChange(opSnapshot: DataSnapshot) {
                adminList.clear()
                opList.clear()
                database.reference.child("user").addValueEventListener(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        adminList.clear()
                        opList.clear()
                        opSnapshot.children.forEach{
                            val username = it.key
                            val opNum = it.value.toString()
                            Log.e("username", username!!)
                            Log.e("opNum", opNum)
                            val fname = snapshot.child("$username/fname").value.toString()
                            val lname = snapshot.child("$username/lname").value.toString()
                            Log.e("fname", fname)
                            if(opNum == "1"){
                                adminList.add(Operator(username, fname, lname, opNum))
                                adminAdapter.notifyDataSetChanged()
                                Log.e("opNum", opNum)
                            }
                            else if(opNum == "0"){
                                opList.add(Operator(username, fname, lname, opNum))
                                opAdapter.notifyDataSetChanged()
                            }
                        }

                    }
                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }
        mRef.addValueEventListener(mRefListener)
    }

    private fun removeOperator(){
        val delUsers = ArrayList<String>()
        binding.lstViewOperator.forEach {
            val check = it.findViewById<CheckBox>(R.id.checkOp).isChecked
            if(check){
                delUsers.add(it.findViewById<TextView>(R.id.txtOpUsername).text.toString())
            }
        }
        if(delUsers.isEmpty()){
            Toast.makeText(context, "Hãy chọn ít nhất 1 người dùng!", Toast.LENGTH_SHORT).show()
        }
        else{
            AlertDialog.Builder(this.requireContext())
                .setTitle("Xóa người điều khiển")
                .setMessage("Sau khi xóa những người dùng được chọn, họ không còn quyền điều khiển hệ thống. Bạn muốn tiếp tục?")
                .setPositiveButton("Có") { dialogInterface: DialogInterface, i: Int ->
                    mRef.removeEventListener(mRefListener)
                    delUsers.forEach {
                        database.reference.child("wateringSystem/${Session.sysID}/operator/$it").removeValue()
                    }
                    mRef.addValueEventListener(mRefListener)
                }
                .setNegativeButton("Không"){ dialogInterface: DialogInterface, i:Int->}
                .show()
        }

    }
}