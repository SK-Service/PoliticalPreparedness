package com.example.android.politicalpreparedness.repository

import android.util.Log
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.CivicsApiService
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.util.getEndOfDateRange
import com.example.android.politicalpreparedness.util.getTodayDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception

const val TAG = "ElectionRepo"
class ElectionRepo (private val database: ElectionDatabase) {
    private lateinit var asteroidListDB: List<Election>

    suspend fun refreshElectionList (): List<Election> {
        Log.i(TAG, "inside refreshElectionList")
        withContext(Dispatchers.IO) {
            Log.i(TAG, "inside  the refreshElectionList-Dispatcher.IO")
            val startTodayDate = getTodayDate()
            val dateAfterNext7Days = getEndOfDateRange()

            try {
                Log.i(TAG, "Before call to Civic API")
                val electionListFromCivic = CivicsApi.electionRetrofitService.getCivicElectionList()
                Log.i(TAG, "After the Civic API call")
                Log.i(TAG, "ElectionList:${electionListFromCivic}")
                Log.i(TAG, "No Database insert exist yet")
//                Log.i("AsteroidRepo", "After call to NASA API: number of asteroids:" +
//                        "                               <${asteroidListNasa.length}>")
//                Log.i("AsteroidRepo", "Before parsing the JSON String")
//                val asteroidListNasa_arraylist = parseAsteroidsJsonResult(JSONObject(asteroidListNasa))
//                Log.i("AsteroidRepo", "After parsing the JSON String-" +
//                        "Number of Asteroids:<${asteroidListNasa_arraylist.size}>")
//
//                Log.i("AsteroidRepo", "Before call to insertAll DB")
//                database.asteroidDao.insertAll(*asteroidListNasa_arraylist.toDatabaseModel())
//                Log.i("AsteroidRepo", "After call to Database for Insert-All")
            } catch (e: Exception) {
                //HTTP Exception are not reflected back to the caller
                // Any other system error the caller should handle
                e.printStackTrace()
            }

            Log.i("AsteroidRepo", "Before call to fetch from DB")
//            asteroidListDB = database.asteroidDao.getAsteroidListByFilter(
//                startTodayDate,dateAfterNext7Days)
            Log.i("AsteroidRepo", "After fetch from DB:<${asteroidListDB.size}>")
        }
        return asteroidListDB
    }
}
