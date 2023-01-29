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

    //_cacheAddressAvailable - Keep track whether cached address is available to be used
    // with voterinfo. The default value being true indicating cache not available
    private var _cacheAddressNotAvailable = MutableLiveData<Boolean>()
    val cacheAddressNotAvailable: LiveData<Boolean>
        get() = _cacheAddressNotAvailable


    var address: Address? = Address("","","","","")
    init {
        Log.i(TAG1, "inside init - where VoterInfo data is fetched from google api")

        viewModelScope.launch {

            _voterInfoAPICallStatus.value = VoterInfoCivicApiStatus.LOADING
            try {
                    Log.i(TAG1, "Before retrieveAddress call")

                    address= VoterInfoRepo(ElectionDatabase.getInstance(application))
                    .retrieveAddress()

                    Log.i(TAG1, "After retrieveAddress call")

                    if (address == null ) {
                        Log.i(TAG1, "Cached addressed has been retrieved")
                        _cacheAddressNotAvailable.value = true
                    } else {
                        _cacheAddressNotAvailable.value = false
                    }
            } catch (e: Exception){
                Log.i(TAG1, "After VoterInfoRepo retrieve address call and inside catch exception")
                e.printStackTrace()
                _voterInfoAPICallStatus.value = VoterInfoCivicApiStatus.ERROR
            }

            try {
                //Call VoterInfoRepo to get the latest voter info from google civic api
                    //_cacheAddressNotAvailable == false means that cache is available
                Log.i(TAG1, "_cacheAddressNotAvailable:<${_cacheAddressNotAvailable.value}>")
                if(_cacheAddressNotAvailable.value == false ) {
                    Log.i(TAG1, "Under this condition we'll like to retrieve VoterInfo from civic api")
                    _voterInfo.value  = address?.let {
                        VoterInfoRepo(ElectionDatabase.getInstance(application))
                            .refreshVoterInfo(selectedElection, it.toFormattedString())
                    }

                }

                updateFollowElectionStatus(selectedElection)

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

            Log.i(TAG1, "updateFollowElectionStatus -electionSaved:" +
                    " ${electionSaved}")

            _followElection.value = electionSaved

            Log.i(TAG1, "updateFollowElectionStatus -_followElection.value:" +
                    " ${_followElection.value}")
        }
    }

    fun resetVoterInfoAPICallStatus() {
        _voterInfoAPICallStatus.value = VoterInfoCivicApiStatus.DONE
    }

}