package com.example.android.politicalpreparedness.util

import android.util.Log
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.TAG_RVM

    fun checkAddressGoodForSearch(address: Address) : Boolean {
        var addressValidity = false
        Log.i(TAG_RVM, "Address Line1:<${address.line1}>")
        Log.i(TAG_RVM, "City1:<${address.city}>")
        Log.i(TAG_RVM, "zip:<${address.zip}>")

        if (checkCityValidity(address.city) || checkAddressLine1Validity(address.line1) ||
            checkUSAZipValidity(address.zip)) {
            addressValidity = true
        }
        return addressValidity
    }

    private fun checkUSAZipValidity(zip: String): Boolean {
        var zipValidity = false
        if (!zip.isNullOrEmpty()) {
            zip.trimStart()
            zip.trimEnd()
            if (zip.length == 5) {
                if (zip.toDoubleOrNull() != null) {
                    zipValidity = true
                }
            }
        }
        return zipValidity
    }

    private fun checkCityValidity(city: String) : Boolean {
        var cityValidity = false
        if (!city.isNullOrEmpty()) {
            city.trimStart()
            city.trimEnd()
            if(city.length >= 3) {
                cityValidity = true
            }
        }
        return cityValidity
    }

    private fun checkAddressLine1Validity(line1: String) : Boolean {
        var addressValidity = false
        Log.i(TAG_RVM, "Address Line1:<${line1}>")
        if(!line1.isNullOrEmpty()) {
            Log.i(TAG_RVM, "Address line 1 is not null")
            line1.trimEnd()
            line1.trimStart()
            if (!line1.isEmpty() || line1 != "\n") {
                Log.i(TAG_RVM, "Address line 1 is not empty or new line")
                addressValidity = true
            }
        }
        return addressValidity
    }
