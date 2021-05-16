package com.example.gardbot.model

class SoilHistory {
    var time : String? = null
    var value : String? = null

    constructor(){}

    constructor(time: String, value: String){
        this.time = time
        this.value = value
    }
}