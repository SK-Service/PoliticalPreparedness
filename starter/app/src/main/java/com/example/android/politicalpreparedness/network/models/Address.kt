package com.example.android.politicalpreparedness.network.models

import android.content.Intent
import android.os.Parcelable
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.android.politicalpreparedness.representative.TAG_R
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "address_table")
data class Address (
    @ColumnInfo(name = "address_line1")var line1: String,
    @ColumnInfo(name = "address_line2")var line2: String? = null,
    @ColumnInfo(name = "city")var city: String,
    @ColumnInfo(name = "state")var state: String,
    @PrimaryKey @ColumnInfo(name = "zip")var zip: String
): Parcelable
{
    fun toFormattedString(): String {
        var output = line1.plus("\n")
        if (!line2.isNullOrEmpty()) output = output.plus(line2).plus("\n")
        output = output.plus("$city, $state $zip")
        return output
    }
}



