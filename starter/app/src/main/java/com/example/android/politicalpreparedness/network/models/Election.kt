package com.example.android.politicalpreparedness.network.models

import android.os.Parcelable
import androidx.room.*
import com.squareup.moshi.*
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
@Entity(tableName = "election_table")
data class Election(
        @PrimaryKey val id: Int,
        @ColumnInfo(name = "name")val name: String,
        @ColumnInfo(name = "electionDay")val electionDay: Date,
        @ColumnInfo(name = "isSaved") var isSaved: Boolean = false,
        @Embedded(prefix = "division_") @Json(name="ocdDivisionId") val division: Division
) :Parcelable

@Entity(tableName = "saved_election_table")
data class SavedElection(
        @PrimaryKey val id: Int,
        @ColumnInfo(name = "name")val name: String,
        @ColumnInfo(name = "electionDay")val electionDay: Date,
        @ColumnInfo(name = "isSaved") var isSaved: Boolean = false,
        @Embedded(prefix = "division_") @Json(name="ocdDivisionId") val division: Division
)

fun Election.copyToSavedElection() = SavedElection (
                      id = id,
                      name = name,
                      electionDay = electionDay,
                      isSaved = isSaved,
                      division = division)

fun List<SavedElection>.asElectionList(): List<Election> {
    return map {
        Election(
            id = it.id,
            name = it.name,
            electionDay = it.electionDay,
            isSaved = it.isSaved,
            division = it.division
        )
    }
}