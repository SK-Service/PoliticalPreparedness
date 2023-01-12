package com.example.android.politicalpreparedness.network.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class VoterInfo (
    var stateName : String,
    var votingLocationURL: String,
    var ballotInfoURL: String,
    var address: String) : Parcelable