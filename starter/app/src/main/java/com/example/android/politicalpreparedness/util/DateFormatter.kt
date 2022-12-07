package com.example.android.politicalpreparedness.util

import java.text.SimpleDateFormat
import java.util.*


fun getTodayDate(): String {
    val calendar = Calendar.getInstance()
    val timeNow = calendar.time
    val formattedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return formattedDate.format(timeNow)
}

fun getEndOfDateRange() : String {
    val calendar = Calendar.getInstance()
    //Data Range is for 1 and half years - which is approximately 548 days
    calendar.add(Calendar.DAY_OF_YEAR, 548)
    val timeNow = calendar.time
    val formattedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return formattedDate.format(timeNow)
}