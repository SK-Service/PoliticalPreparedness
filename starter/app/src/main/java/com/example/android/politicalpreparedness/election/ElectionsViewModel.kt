package com.example.android.politicalpreparedness.election

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.PrimaryKey
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.models.Division
import com.example.android.politicalpreparedness.network.models.Election
import com.squareup.moshi.Json
import kotlinx.coroutines.launch
import java.util.*

//TODO: Construct ViewModel and provide election datasource
enum class ElectionCivicApiStatus {LOADING, ERROR, DONE }
const val TAG = "ElectionsViewModel"
class ElectionsViewModel(datasource: ElectionDao, application: Application): AndroidViewModel(application) {

    //TODO: Create live data val for upcoming elections
    //Managed data for list of elections
    private var _listOfElections = MutableLiveData<MutableList<Election>>(mutableListOf())
    val listOfElection: LiveData<MutableList<Election>>
        get() = _listOfElections

    //Managed data for API retrieval status
    private var _civicAPICallStatus = MutableLiveData<ElectionCivicApiStatus>()
    val civicAPICallStatus: LiveData<ElectionCivicApiStatus>
        get() = _civicAPICallStatus

    //TODO: Create live data val for saved elections

    //TODO: Create val and functions to populate live data for upcoming elections from the API and saved elections from local database

    //init is called to populate the database or cache at the start, with the list of elections
    // and also populate the list of saved elections if there are any
    init {
        Log.i(TAG, "inside init")
        viewModelScope.launch {

            _civicAPICallStatus.value = ElectionCivicApiStatus.LOADING
            try {
//@TODO Change the code to inclue the repository pattern and actual service call
//                _listOfElections.value = AsteroidRepo(ElectionDatabase.getDatabase(application))
//                    .refreshAsteroidList()
//                    .toMutableList()

    //for referirng the Election and Division fill @TODO DELETE
//                @PrimaryKey val id: Int,
//                @ColumnInfo(name = "name")val name: String,
//                @ColumnInfo(name = "electionDay")val electionDay: Date,
//                @Embedded(prefix = "division_") @Json(name="ocdDivisionId") val division: Division
//                data class Division(
//                    val id: String,
//                    val country: String,
//                    val state: String

                val election1: Election = Election(1, "NYC State Presidency Primary",
                            Date("11/20/2022"), Division("011", "county1", "NYC") )
                val election2: Election = Election(2, "Ohio State Presidency Primary",
                    Date("11/29/2022"), Division("022", "county2", "Ohio") )

                _listOfElections.value = mutableListOf(election1, election2)
                _civicAPICallStatus.value = ElectionCivicApiStatus.DONE
            } catch (e: Exception){
                Log.i(TAG, "After ElectionRepo call and inside catch exception")
                e.printStackTrace()
                _civicAPICallStatus.value = ElectionCivicApiStatus.ERROR
            }

        }

    }
    //@TODO potentially can change
    fun displayElectiondDetails(election: Election) {
       // _navigateToDetail.value = asteroid
    }

    //@TODO potentially can change
    fun displayElectionDetailsComplete() {
       // _navigateToDetail.value = null
    }
    //TODO: Create functions to navigate to saved or upcoming election voter info

}