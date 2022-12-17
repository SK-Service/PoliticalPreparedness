package com.example.android.politicalpreparedness.network.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class VoterInfo (
    val electionID: Int,
    val electionTitle: String,
    val electionDay: Date,
    var votingLocationURL: String,
    var ballotInfoURL: String) : Parcelable