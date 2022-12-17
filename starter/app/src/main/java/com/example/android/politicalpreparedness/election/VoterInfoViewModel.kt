package com.example.android.politicalpreparedness.election

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.database.ElectionDatabase
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

    lateinit var voterInfoResponse: VoterInfoResponse
    init {
        Log.i(TAG1, "inside init - where VoterInfo data is fetched from google api")
        viewModelScope.launch {

            _voterInfoAPICallStatus.value = VoterInfoCivicApiStatus.LOADING
            try {
                //Call ElectionRepo to get the latest refreshed list from source or cache
                voterInfoResponse  = VoterInfoRepo(ElectionDatabase.getInstance(application))
                    .refreshVoterInfo()
                Log.i(TAG1, "BallotInfoURL: " +
                  "${voterInfoResponse.state?.get(0)?.electionAdministrationBody?.ballotInfoUrl}")
                _voterInfoAPICallStatus.value = VoterInfoCivicApiStatus.DONE

                val voterInfo = VoterInfo (selectedElection.id, "XYZ Election", Date("12/16/2022"),
                    voterInfoResponse.state?.get(0)?.
                    electionAdministrationBody?.votingLocationFinderUrl.toString(),
                    voterInfoResponse.state?.get(0)?.
                    electionAdministrationBody?.ballotInfoUrl.toString())
                _voterInfo.value = voterInfo

                Log.i(TAG1, "VoterInfo Live Data Value: Voting Location URL:" +
                        "                ${_voterInfo.value?.ballotInfoURL}")

            } catch (e: Exception){
                Log.i(TAG, "After ElectionRepo call and inside catch exception")
                e.printStackTrace()
                _voterInfoAPICallStatus.value = VoterInfoCivicApiStatus.ERROR
            }

        }
//        Log.i(TAG1, "VoterInfoCivicApiStatus:${_voterInfoAPICallStatus.value}")
//        if (_voterInfoAPICallStatus.value == VoterInfoCivicApiStatus.DONE ) {
//            Log.i(TAG1, "Inside if check VoterInfoCivicApiStatus:" +
//                    "${_voterInfoAPICallStatus.value}")
//            _voterInfo.value?.votingLocationURL  = voterInfoResponse.state?.get(0)?.
//                            electionAdministrationBody?.votingLocationFinderUrl.toString()
//            _voterInfo.value?.ballotInfoURL  = voterInfoResponse.state?.get(0)?.
//                                        electionAdministrationBody?.ballotInfoUrl.toString()
//        }
    }

    //TODO: Add var and methods to populate voter info

    //TODO: Add var and methods to support loading URLs

    //TODO: Add var and methods to save and remove elections to local database
    //TODO: cont'd -- Populate initial state of save button to reflect proper action based on election saved status

    /**
     * Hint: The saved state can be accomplished in multiple ways. It is directly related to how elections are saved/removed from the database.
     */

}