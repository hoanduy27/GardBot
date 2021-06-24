package com.example.gardbot.adapters

import android.content.Context
import android.opengl.Visibility
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.example.gardbot.R

class OperatorAdapter(val operators : ArrayList<Operator>, val ctx : FragmentActivity?) : BaseAdapter(){
    override fun getItem(position: Int): Operator {
        return operators[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return operators.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = View.inflate(ctx, R.layout.layout_op_list, null)
        val opUsername = view.findViewById<TextView>(R.id.txtOpUsername)
        val opName = view.findViewById<TextView>(R.id.txtOpName)
        val checkOp = view.findViewById<CheckBox>(R.id.checkOp)

        opUsername.text = operators[position].username
        opName.text = "${operators[position].lname} ${operators[position].fname}"
        checkOp.visibility = if(operators[position].opNum == "1"){
            View.GONE
        }else{
            View.VISIBLE
        }
        return view
    }
}

class Operator(username : String, fname : String, lname : String, opNum : String){
    var username = username
    var fname = fname
    var lname = lname
    var opNum = opNum
}