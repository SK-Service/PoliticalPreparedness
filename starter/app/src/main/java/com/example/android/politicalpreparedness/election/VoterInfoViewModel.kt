package com.example.android.politicalpreparedness.election

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.VoterInfo
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import com.example.android.politicalpreparedness.repository.ElectionRepo
import com.example.android.politicalpreparedness.repository.VoterInfoRepo
import kotlinx.coroutines.launch
import java.lang.Appendable
import java.util.*

enum class VoterInfoCivicApiStatus {LOADING, ERROR, DONE }
const val TAG1 = "VoterInfoViewModel"
class VoterInfoViewModel(private val selectedElection: Election,
                         private val dataSource: ElectionDao,
                         private val application: Application) : ViewModel() {

    //TODO: Add live data to hold voter info
   //To hold the selected election through app lifecycle
//    private var _selectedElection = MutableLiveData<Election>()
//    val selectedElection: LiveData<Election>
//        get() = _selectedElection

    private var _voterInfo = MutableLiveData<VoterInfo>()
    val voterInfo: LiveData<VoterInfo>
        get() = _voterInfo

    //Managed data for API retrieval status
    private var _voterInfoAPICallStatus = MutableLiveData<VoterInfoCivicApiStatus>()
    val voterInfoAPICallStatus: LiveData<VoterInfoCivicApiStatus>
        get() = _voterInfoAPICallStatus

    //True indicates followElection
    // whereas false indicates Election not being followed or unfollowed
    private var _followElection = MutableLiveData<Boolean>()
    val followElection: LiveData<Boolean>
        get() = _followElection

    var address = Address("","","","","")
    init {
        Log.i(TAG1, "inside init - where VoterInfo data is fetched from google api")

        viewModelScope.launch {

            _voterInfoAPICallStatus.value = VoterInfoCivicApiStatus.LOADING
            try {
                    address= VoterInfoRepo(ElectionDatabase.getInstance(application))
                    .retrieveAddress()
            } catch (e: Exception){
                Log.i(TAG1, "After VoterInfoRepo retrieve address call and inside catch exception")
                e.printStackTrace()
                _voterInfoAPICallStatus.value = VoterInfoCivicApiStatus.ERROR
            }
//which address to use
            //extract the state from the division to see if the stored state is different from the
            //state found in the election division entity
            val division = selectedElection.division
            val stateDelimiter = "state:"
            val state = division.toString().substringAfter(stateDelimiter,"")
                .substringBefore("/")
            Log.i(TAG1, "State extracted from Division:<${state}>")

            try {
                //Call VoterInfoRepo to get the latest voter info from google civic api
                if(state.equals(address?.state)) {
                    _voterInfo.value  = VoterInfoRepo(ElectionDatabase.getInstance(application))
                        .refreshVoterInfo(selectedElection, address.toFormattedString())
                } else {
                    _voterInfo.value  = VoterInfoRepo(ElectionDatabase.getInstance(application))
                        .refreshVoterInfo(selectedElection, state)
                }


                if (!selectedElection.isSaved) {
                    Log.i(TAG1, "Changing Follow Election to false as isSaved is not true")
                    _followElection.value = false
                }

                _voterInfoAPICallStatus.value = VoterInfoCivicApiStatus.DONE

            } catch (e: Exception){
                Log.i(TAG1, "After VoterInfoRepo civic api call and inside catch exception")
                _voterInfoAPICallStatus.value = VoterInfoCivicApiStatus.ERROR
                e.printStackTrace()
            }

        }

    }

    /**
     * Hint: The saved state can be accomplished in multiple ways. It is directly related to how elections are saved/removed from the database.
     */
     fun followElection () {
        viewModelScope.launch {
            Log.i(TAG1, "Election ID:${selectedElection.id}, isSaved:${selectedElection.isSaved}")
            selectedElection.isSaved = true
            VoterInfoRepo(ElectionDatabase.getInstance(application)).
            saveElectionFollowing(selectedElection)
            _followElection.value = true
        }
    }

     fun unFollowElection () {
        viewModelScope.launch {
            Log.i(TAG1, "Election ID:${selectedElection.id}, isSaved:${selectedElection.isSaved}")
//            selectedElection.isSaved = false
            VoterInfoRepo(ElectionDatabase.getInstance(application)).
            deleteElectionFollowing(selectedElection)
            _followElection.value = false
        }
    }

    fun updateFollowElectionStatus(selectedElection: Election)  {

        viewModelScope.launch {
            Log.i(TAG1, "updateFollowElectionStatus - Election ID:${selectedElection.id}, " +
                    "isSaved:${selectedElection.isSaved}")

            val electionSaved = VoterInfoRepo (ElectionDatabase.getInstance(application))
                .isElectionSaved(selectedElection)

            _followElection.value = electionSaved
        }
    }

    fun resetVoterInfoAPICallStatus() {
        _voterInfoAPICallStatus.value = VoterInfoCivicApiStatus.DONE
    }

}