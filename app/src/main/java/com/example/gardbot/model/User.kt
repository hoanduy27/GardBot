package com.example.gardbot.model

class User {
    //private lateinit var username : String
     var email: String? = null
     var fname: String? = null
     var lname: String? = null
     var password: String? = null
     var phoneNumber: String? = null

    constructor(_email: String, _fname : String, _lname: String, _password: String, _phoneNumber: String){
        email = _email
        fname = _fname
        lname = _lname
        password = _password
        phoneNumber = _phoneNumber
    }
}