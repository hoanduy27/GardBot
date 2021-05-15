package Adapters

class Sensor(val _sensorName: String, val _pump: List<Pump>) {
    var sensorName :String = _sensorName
    var pumpList : List<Pump> = _pump
}