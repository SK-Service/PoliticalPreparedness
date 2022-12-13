package com.example.android.politicalpreparedness.network.jsonadapter

import com.example.android.politicalpreparedness.network.models.Division
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

class ElectionAdapter {
    @FromJson
    fun divisionFromJson (ocdDivisionId: String): Division {
        val countryDelimiter = "country:"
        val stateDelimiter = "state:"
        val country = ocdDivisionId.substringAfter(countryDelimiter,"")
                .substringBefore("/")
        val state = ocdDivisionId.substringAfter(stateDelimiter,"")
                .substringBefore("/")
        return Division(ocdDivisionId, country, state)
    }

    @ToJson
    fun divisionToJson (division: Division): String {
        return division.id
    }

    @FromJson
    fun electionDateFromJason(electionDay: String): Date {
        val formatter = SimpleDateFormat("yyyy-MM-dd")
        return formatter.parse(electionDay)
    }

    @ToJson
    fun electionDateToJason(electionDay: Date): String {
        val format = SimpleDateFormat("yyyy-MM-dd")
        return format.format(format)
    }
}