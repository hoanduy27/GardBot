package com.example.gardbot.adapters

class PumpControlBox(val _pumpName: String, val _sensorName: String, val _moisture: String,val _auto:String, val _waterLevel:String,val _pumpId:String) {
    var pumpName = _pumpName;
    var sensorName = _sensorName;
    var moisture = _moisture;
    var auto=_auto;
    var waterLevel= _waterLevel;
    var pumpId = _pumpId;
}