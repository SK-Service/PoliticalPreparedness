package com.example.android.politicalpreparedness.representative.model

import android.os.Parcelable
import com.example.android.politicalpreparedness.network.models.Channel
import com.example.android.politicalpreparedness.network.models.Office
import com.example.android.politicalpreparedness.network.models.Official
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RepresentativeProfile (
        val id: Int,
        val profileImageURL: String,
        val title: String,
        val name: String,
        val party: String,
        val channels: List<Channel>
): Parcelable