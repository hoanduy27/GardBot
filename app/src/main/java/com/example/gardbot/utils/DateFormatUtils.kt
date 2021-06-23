package com.example.gardbot.utils

import java.text.SimpleDateFormat

class DateFormatUtils {
    companion object{
        var datetimeFormat = SimpleDateFormat("dd-MM-yyyy-HH:mm:ss")
        var timeFormat = SimpleDateFormat("HH:mm:ss")
    }
}