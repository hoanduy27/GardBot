package com.example.gardbot.adapters

class Selection(val _header : String, val _body : String, val _footer : String, val _drawable : Int = 0) {
    var header: String = _header
    var body: String = _body
    var footer: String = _footer
    var drawable : Int = _drawable
}