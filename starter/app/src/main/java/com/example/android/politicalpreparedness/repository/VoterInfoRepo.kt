package com.example.android.politicalpreparedness.repository

import android.util.Log
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.CivicsApiService
import com.example.android.politicalpreparedness.network.models.*
import com.example.android.politicalpreparedness.util.getEndOfDateRange
import com.example.android.politicalpreparedness.util.getTodayDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

const val TAG2 = "VoterInfoRepo"
class VoterInfoRepo (private val database: ElectionDatabase) {
    private lateinit var voterInfoResponse: VoterInfoResponse
    private var voterInfo = VoterInfo("","","","")

    suspend fun refreshVoterInfo (selectedElection: Election, address: String): VoterInfo {
        Log.i(TAG2, "inside refreshVoterInfo")
        withContext(Dispatchers.IO) {
            Log.i(TAG2, "inside  the refreshVoterInfo-Dispatcher.IO")

            try {
                Log.i(TAG, "Before call to Civic API")
                voterInfoResponse  =
                    CivicsApi.electionRetrofitService.getCivicVoterInformation(address,
                                                    selectedElection.id)
                Log.i(TAG, "After the Civic API call")
                if (voterInfoResponse.state != null) {
                    val voterInfoState = getVoterInfoState(voterInfoResponse.state!!)
                    if (voterInfoState != null ) {
                        voterInfo.stateName = voterInfoState.name?:""
                        voterInfo.ballotInfoURL = voterInfoState.electionAdministrationBody.ballotInfoUrl?: ""
                        voterInfo.votingLocationURL = voterInfoState.electionAdministrationBody.votingLocationFinderUrl?: ""
                        voterInfo.address = voterInfoState.electionAdministrationBody.correspondenceAddress?.toFormattedString()
                            ?: ""
                    } else {
                        voterInfo.stateName =""
                        voterInfo.ballotInfoURL = ""
                        voterInfo.votingLocationURL = ""
                        voterInfo.address = ""

                    }
                }

////@TODO TO BE DELETED CODE - START
////        //@TODO - TO BE DELETED - Instantiating two election objects
//            Log.i(TAG2, "Instantiating State")
//                val electionAdministrationBody: AdministrationBody = AdministrationBody("Secretary of State", "",
//                   "https://voterstatus.sos.ca.gov", "https://www.sos.ca.gov/elections/ballot-status/wheres-my-ballot/",
//                            Address("","", "","","") )
//                val state: State = State("California", electionAdministrationBody )
//                val election: Election = Election(1, "NYC State Presidency Primary",
//                    Date("11/20/2022"), false,
//                    Division("011", "county1", "NYC") )
//                voterInfoResponse = VoterInfoResponse(election = election, state = listOf(state))

//@TODO TO BE DELETED CODE - END

            } catch (e: Exception) {
                //HTTP Exception are not reflected back to the caller
                // Any other system error the caller should handle
                e.printStackTrace()
                throw e
            }

        }
        return voterInfo
    }
    private fun getVoterInfoState (listOfStates: List<State>) : State? {
        return listOfStates.firstOrNull()
    }

    suspend fun isElectionSaved (selectedElection: Election): Boolean {
        var isSavedElection  = false
        withContext(Dispatchers.IO) {
            Log.i(TAG2, "Inside isElectionSaved")
            Log.i(TAG2, "Election ID:${selectedElection.id}, " +
                                    "isSaved:${selectedElection.isSaved}")

             val savedElection =
                database.savedElectionDao.getSavedElection(selectedElection.id)

            if (savedElection != null ){
                Log.i(TAG2, "Found a record in the Saved Election DB")
                isSavedElection = true
            }
        }
        return isSavedElection
    }

    suspend fun saveElectionFollowing (selectedElection: Election) {
        withContext(Dispatchers.IO) {
            Log.i(TAG2, "saveElectionFollowing")
            Log.i(TAG2, "Election ID:${selectedElection.id}, isSaved:${selectedElection.isSaved}")
            val savedElection = selectedElection.copyToSavedElection()
            database.savedElectionDao.addSavedElection(savedElection)
        }
    }

    suspend fun deleteElectionFollowing (selectedElection: Election) {
        withContext(Dispatchers.IO) {
            Log.i(TAG2, "deleteElectionFollowing")
            Log.i(TAG2, "Election ID:${selectedElection.id}, isSaved:${selectedElection.isSaved}")
            val savedElection = selectedElection.copyToSavedElection()
            database.savedElectionDao.deleteSavedElection(savedElection)
        }
    }

    suspend fun retrieveAddress(): Address {
        Log.i(TAG2, "inside retrieveAddress")
        var address = Address("","","","","")
        withContext(Dispatchers.IO) {
            Log.i(TAG2, "inside  the retrieveAddress-Dispatcher.IO")
            try {
                //Retrieve address - since only one address is present on the table all the time
                // only one address is going to be returned
                address = database.savedAddressDao.getAddress()
            } catch (e: Exception) {
                //Saving the address for the purpose of caching is not an important function
                // In case there is an error, app will absorb it here
                e.printStackTrace()
            }
        }
        return  address
    }


}
