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
import com.example.android.politicalpreparedness.network.models.SavedElection
import com.example.android.politicalpreparedness.network.models.asElectionList
import com.example.android.politicalpreparedness.repository.ElectionRepo
import com.squareup.moshi.Json
import kotlinx.coroutines.launch
import java.util.*

//TODO: Construct ViewModel and provide election datasource
enum class ElectionCivicApiStatus {LOADING, ERROR, DONE }
const val TAG = "ElectionsViewModel"
class ElectionsViewModel(datasource: ElectionDao, application: Application):
                                                    AndroidViewModel(application) {

    //TODO: Create live data val for upcoming elections
    //Managed data for list of elections
    private var _listOfElections = MutableLiveData<MutableList<Election>>(mutableListOf())
    val listOfElection: LiveData<MutableList<Election>>
        get() = _listOfElections

    //Managed data for list of elections
    private var _listOfSavedElections = MutableLiveData<MutableList<Election>>(mutableListOf())
    val listOfSavedElections: LiveData<MutableList<Election>>
        get() = _listOfSavedElections

    //Managed data for API retrieval status
    private var _civicAPICallStatus = MutableLiveData<ElectionCivicApiStatus>()
    val civicAPICallStatus: LiveData<ElectionCivicApiStatus>
        get() = _civicAPICallStatus

    //Managed data for controlling navigation to the Voter Information Screen
    private val _navigateToVoterInfo = MutableLiveData<Election>()
    val navigateToVoterInfo: LiveData<Election>
        get() = _navigateToVoterInfo

    //TODO: Create live data val for saved elections

    //TODO: Create val and functions to populate live data for upcoming elections from the API and saved elections from local database

    //init is called to populate the database or cache at the start, with the list of elections
    // and also populate the list of saved elections if there are any
    init {
        Log.i(TAG, "inside init - where data fetch and caching using Room DB will take place")
        viewModelScope.launch {

            _civicAPICallStatus.value = ElectionCivicApiStatus.LOADING
            try {
                //Call ElectionRepo to get the latest refreshed list from source or cache
                val electionLists = ElectionRepo(ElectionDatabase.getInstance(application))
                    .refreshElectionList()
                _listOfElections.value = electionLists.electionList.toMutableList()

                Log.i(TAG, "Election List ${_listOfElections.value?.size}")

                _listOfSavedElections.value = electionLists.savedElectionList.
                                                    asElectionList().toMutableList()

                Log.i(TAG, "Saved Elections ${_listOfSavedElections.value?.size}")

//                Log.i(TAG, "Filter out the elections with isSavedAsTrue")
//                val filteredList = listOfElection.value?.filter{it.isSaved}
//                if (filteredList != null) {
//                    _listOfSavedElections.value = filteredList.toMutableList()
//                }
//                Log.i(TAG, "completed filtering of saved list of elections")
//    //@TODO - TO BE DELETE - HARD CODED DATA
//        //@TODO - TO BE DELETED - Instantiating two election objects
//            Log.i(TAG, "Instantiating election 1 NYC & election 2 Ohio")
//                val election1: Election = Election(1, "NYC State Presidency Primary",
//                            Date("11/20/2022"), Division("011", "county1", "NYC") )
//                val election2: Election = Election(2, "Ohio State Presidency Primary",
//                    Date("11/29/2022"), Division("022", "county2", "Ohio") )
//                Log.i( TAG, "assigning list to listOfElections.value")
//                _listOfElections.value = mutableListOf(election1, election2)

                _civicAPICallStatus.value = ElectionCivicApiStatus.DONE
            } catch (e: Exception){
                Log.i(TAG, "After ElectionRepo call and inside catch exception")
                e.printStackTrace()
                _civicAPICallStatus.value = ElectionCivicApiStatus.ERROR
            }

        }

    }

    //Controller to control navigation to the Voter Info Screen
    fun displayVoterInfo(election: Election) {
        _navigateToVoterInfo.value = election
    }

    //Controller to control navigation to the Voter Info Screen
    fun displayVoterInfoComplete() {
        _navigateToVoterInfo.value = null
    }

    //function to retrieve elections from both election repo
    fun retrieveElectionsFromRepos() {
            Log.i(TAG, "inside retrieveElectionsFromRepos")
            viewModelScope.launch {
                _civicAPICallStatus.value = ElectionCivicApiStatus.LOADING
                try {
                    //Call ElectionRepo to get the latest refreshed list from source or cache
                    val electionLists = ElectionRepo(ElectionDatabase.getInstance(getApplication()))
                        .refreshElectionList()
                    _listOfElections.value = electionLists.electionList.toMutableList()

                    Log.i(TAG, "Election List ${_listOfElections.value?.size}")

                    _listOfSavedElections.value = electionLists.savedElectionList.
                    asElectionList().toMutableList()

                    Log.i(TAG, "Saved Elections ${_listOfSavedElections.value?.size}")

//                Log.i(TAG, "Filter out the elections with isSavedAsTrue")
//                val filteredList = listOfElection.value?.filter{it.isSaved}
//                if (filteredList != null) {
//                    _listOfSavedElections.value = filteredList.toMutableList()
//                }
//                Log.i(TAG, "completed filtering of saved list of elections")
//    //@TODO - TO BE DELETE - HARD CODED DATA
//        //@TODO - TO BE DELETED - Instantiating two election objects
//            Log.i(TAG, "Instantiating election 1 NYC & election 2 Ohio")
//                val election1: Election = Election(1, "NYC State Presidency Primary",
//                            Date("11/20/2022"), Division("011", "county1", "NYC") )
//                val election2: Election = Election(2, "Ohio State Presidency Primary",
//                    Date("11/29/2022"), Division("022", "county2", "Ohio") )
//                Log.i( TAG, "assigning list to listOfElections.value")
//                _listOfElections.value = mutableListOf(election1, election2)

                    _civicAPICallStatus.value = ElectionCivicApiStatus.DONE
                } catch (e: Exception){
                    Log.i(TAG, "After ElectionRepo call and inside catch exception")
                    e.printStackTrace()
                    _civicAPICallStatus.value = ElectionCivicApiStatus.ERROR
                }

            }

    }

}