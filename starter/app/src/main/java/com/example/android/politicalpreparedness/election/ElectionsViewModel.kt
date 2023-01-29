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

enum class ElectionCivicApiStatus {LOADING, ERROR, DONE }
const val TAG = "ElectionsViewModel"
class ElectionsViewModel(datasource: ElectionDao, application: Application):
                                                    AndroidViewModel(application) {

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

                    _civicAPICallStatus.value = ElectionCivicApiStatus.DONE
                } catch (e: Exception){
                    Log.i(TAG, "After ElectionRepo call and inside catch exception")
                    e.printStackTrace()
                    _civicAPICallStatus.value = ElectionCivicApiStatus.ERROR
                }

            }

    }

}