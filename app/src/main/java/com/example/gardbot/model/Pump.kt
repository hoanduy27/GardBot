package com.example.gardbot.model

import android.os.Parcelable
import java.io.Serializable

class Pump : Serializable{
    var name : String? = null
    var soilMoistureID : String? = null
    var waterLevel : String? = null

    constructor(){}
    constructor(_name: String, _soilMoistureiD : String, _waterLevel: String){
        name = _name
        soilMoistureID = _soilMoistureiD
        waterLevel = _waterLevel
    }

}