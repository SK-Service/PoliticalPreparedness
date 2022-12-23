package com.example.android.politicalpreparedness.repository

import android.util.Log
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.models.*
import com.example.android.politicalpreparedness.util.getEndOfDateRange
import com.example.android.politicalpreparedness.util.getTodayDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

const val TAG = "ElectionRepo"
class ElectionRepo (private val database: ElectionDatabase) {
    private lateinit var electionListDB: List<Election>
    private lateinit var savedElectionListDB: List<SavedElection>
    private lateinit var electionLists: ElectionLists

//    suspend fun refreshElectionList (): List<Election> {
      suspend fun refreshElectionList (): ElectionLists {
        Log.i(TAG, "inside refreshElectionList")
        withContext(Dispatchers.IO) {
            Log.i(TAG, "inside  the refreshElectionList-Dispatcher.IO")
            val startTodayDate = getTodayDate()
            val dateAfterNext7Days = getEndOfDateRange()

            try {
//                Log.i(TAG, "Before call to Civic API")
//                val electionListFromCivic = CivicsApi.electionRetrofitService.getCivicElectionList()
//                Log.i(TAG, "After the Civic API call")
//                Log.i(TAG, "ElectionKind:${electionListFromCivic.kind}")
//                Log.i(TAG, "ElectionKind:${electionListFromCivic.elections}")
//                Log.i(TAG, "No Database insert exist yet")
//

//                Log.i("AsteroidRepo", "After call to NASA API: number of asteroids:" +
//                        "                               <${asteroidListNasa.length}>")
//                Log.i("AsteroidRepo", "Before parsing the JSON String")
//                val asteroidListNasa_arraylist = parseAsteroidsJsonResult(JSONObject(asteroidListNasa))
//                Log.i("AsteroidRepo", "After parsing the JSON String-" +
//                        "Number of Asteroids:<${asteroidListNasa_arraylist.size}>")
//
//@TODO TO BE DELETED CODE
//        //@TODO - TO BE DELETED - Instantiating two election objects
            Log.i(TAG, "Instantiating election 1 NYC & election 2 Ohio")
                val election1: Election = Election(1, "NYC State Presidency Primary",
                    Date("11/20/2022"), false,
                            Division("011", "county1", "NYC") )
                val election2: Election = Election(2, "Ohio State Presidency Primary",
                    Date("11/29/2022"), false,
                            Division("022", "county2", "Ohio") )
                val electionArraylist: Array<Election> = arrayOf(election1, election2)

                 Log.i(TAG, "Before call to insertAll DB")
                 database.electionDao.insertAll(*electionArraylist)
                Log.i(TAG, "After call to Database for Insert-All")
            } catch (e: Exception) {
                //HTTP Exception are not reflected back to the caller
                // Any other system error the caller should handle
                e.printStackTrace()
            }

            Log.i(TAG, "Before call to fetch from ElectionDB")
            electionListDB = database.electionDao.getElectionList()
            Log.i(TAG, "After fetch from ElectionDB:<${electionListDB.size}>")

            Log.i(TAG, "Before call to fetch from Saved Election DB")
            savedElectionListDB = database.savedElectionDao.getSavedElectionList()
            Log.i(TAG, "After fetch from SavedElectionDB:<${savedElectionListDB.size}>")

            electionLists = ElectionLists(electionListDB, savedElectionListDB)
//            electionLists.electionList = electionListDB
//            electionLists.savedElectionList = savedElectionListDB
        }
        return electionLists
    }


}
