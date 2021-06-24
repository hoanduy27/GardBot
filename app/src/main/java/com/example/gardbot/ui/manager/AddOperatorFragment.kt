package com.example.gardbot.ui.manager

import android.content.DialogInterface
import android.os.Bundle
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
import com.example.gardbot.databinding.FragmentAddOperatorBinding
import com.example.gardbot.model.Session
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class AddOperatorFragment : Fragment() {
    private lateinit var _binding : FragmentAddOperatorBinding
    private var opList = ArrayList<Operator>()
    private lateinit var opAdapter : OperatorAdapter
    private val database = Firebase.database
    private val mRef = database.reference.child("user")
    private lateinit var mRefListener : ValueEventListener

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //var view = inflater.inflate(R.layout.fragment_add_operator, container, false)
        _binding = FragmentAddOperatorBinding.inflate(inflater, container, false)

        // fill adapter
        opAdapter = OperatorAdapter(opList, activity)
        binding.lstViewNewOperator.adapter = opAdapter

        viewOperators()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        binding.btnAddConfirm.setOnClickListener {
            addOperators()
        }
        binding.btnAddCancel.setOnClickListener {
//            val intent = Intent(this.context, SystemActivity::class.java)
//            startActivity(intent)
            requireActivity().onBackPressed()
        }

    }
    private fun viewOperators(){
        mRefListener = object:ValueEventListener{
            override fun onDataChange(userSnapshot: DataSnapshot) {
                opList.clear()
                database.reference.child("wateringSystem/${Session.sysID}/operator").addValueEventListener(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        opList.clear()
                        userSnapshot.children.forEach{
                            val username = it.key
                            if(!snapshot.hasChild(username!!)){
                                val fname = it.child("fname").value.toString()
                                val lname = it.child("lname").value.toString()
                                opList.add(Operator(username, fname, lname, "0"))
                                opAdapter.notifyDataSetChanged()
                                Log.e("fname", fname)
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

    private fun addOperators(){
        val addedUsers = ArrayList<String>()
        binding.lstViewNewOperator.forEach {
            val check = it.findViewById<CheckBox>(R.id.checkOp).isChecked
            if(check){
                addedUsers.add(it.findViewById<TextView>(R.id.txtOpUsername).text.toString())
            }
        }
        if(addedUsers.isEmpty()){
            Toast.makeText(context, "Hãy chọn ít nhất 1 người dùng!", Toast.LENGTH_SHORT).show()
        }
        else{
            AlertDialog.Builder(this.requireContext())
                .setTitle("Thêm người điều khiển")
                .setMessage("Xác nhận thêm những người dùng này vào hệ thống?")
                .setPositiveButton("Có") { dialogInterface: DialogInterface, i: Int ->
                    mRef.removeEventListener(mRefListener)
                    addedUsers.forEach {
                        database.reference.child("wateringSystem/${Session.sysID}/operator/$it").setValue("0")
                    }
                    mRef.addValueEventListener(mRefListener)
                }
                .setNegativeButton("Không"){ dialogInterface: DialogInterface, i:Int->}
                .show()
        }

    }
}