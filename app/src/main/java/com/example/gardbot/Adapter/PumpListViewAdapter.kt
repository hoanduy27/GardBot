package com.example.gardbot.Adapter

import android.content.Context
import android.widget.ArrayAdapter
import com.example.gardbot.Model.Pump

class PumpListViewAdapter (var ctx: Context, var resource : Int, var Item : ArrayList<Pump>) : ArrayAdapter<Pump>(ctx, resource, Item){
    
}