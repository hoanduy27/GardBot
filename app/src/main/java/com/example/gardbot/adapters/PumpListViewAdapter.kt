package com.example.gardbot.adapters

import android.content.Context
import android.widget.ArrayAdapter
import com.example.gardbot.model.Pump

class PumpListViewAdapter (var ctx: Context, var resource : Int, var Item : ArrayList<Pump>) : ArrayAdapter<Pump>(ctx, resource, Item){
    
}