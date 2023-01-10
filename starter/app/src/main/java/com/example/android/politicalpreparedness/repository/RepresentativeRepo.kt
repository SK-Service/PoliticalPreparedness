package com.example.android.politicalpreparedness.repository

import android.app.Application
import android.util.Log
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.*
import com.example.android.politicalpreparedness.representative.model.Representative
import com.example.android.politicalpreparedness.representative.model.RepresentativeProfile
import com.example.android.politicalpreparedness.util.getEndOfDateRange
import com.example.android.politicalpreparedness.util.getTodayDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList
import android.content.Context
import com.example.android.politicalpreparedness.network.jsonadapter.ElectionAdapter
import com.example.android.politicalpreparedness.representative.RepresentativeCivicApiStatus
import com.example.android.politicalpreparedness.representative.TAG_RVM
import org.json.JSONArray

const val TAG_Repo1 = "RepresentativeRepo"
class RepresentativeRepo (private val application: Application,
                          private val database: ElectionDatabase) {
//    private lateinit var electionListDB: List<Election>
//    private lateinit var savedElectionListDB: List<SavedElection>
    private lateinit var representativeLists: List<RepresentativeProfile>

    suspend fun refreshRepresentativeList (address: Address): List<RepresentativeProfile> {
        Log.i(TAG_Repo1, "inside refreshRepresentativeList")
        withContext(Dispatchers.IO) {
            Log.i(TAG, "inside  the refreshRepresentativeList-Dispatcher.IO")

            try {
                Log.i(TAG, "Before call to Civic Representative API")
                Log.i(TAG, "Formatted Address:<${address.toFormattedString()}>")
                //TODO - This is has to be further anlyzed to ensure that the complex Json object is dealt with
                val representativeResponse = CivicsApi.electionRetrofitService.
                                getCivicRepresentatives(address.toFormattedString())
//                val responseJSONObject = CivicsApi.electionRetrofitService.
//                getCivicRepresentatives(address.toFormattedString())
                Log.i(TAG, "After the Civic API call")
                Log.i(TAG, "No Database insert exist yet")

                //TODO - TO BE DELETED - HARD CODED JSON
//                val repProfileJsonString: String = loadTestRepreentativeProfile(application)

//                val repProfileJsonString = responseJSONObject.toString()
//                val representativeListFromCivic = getCiviRepAPIRepresentativeResponse(
//                                                    JSONObject(repProfileJsonString))

                //Response has got offices and for each office,there are one ore more official
                //object
//                representativeLists = getRepresentativesProfile (representativeListFromCivic)
                representativeLists = getRepresentativesProfile (representativeResponse)
            } catch (e: Exception) {
                //HTTP Exception are not reflected back to the caller
                // Any other system error the caller should handle
                e.printStackTrace()
            }

        }
        return representativeLists
    }

    suspend fun saveAddress(address: Address) {
        Log.i(TAG_Repo1, "inside saveAddress")
        withContext(Dispatchers.IO) {
            Log.i(TAG_Repo1, "inside  the saveAddress-Dispatcher.IO")
            try {
                    //Remove all existing address to ensure that there is only one registered address
                    database.savedAddressDao.removeAllAddress()
                    //add the new address
                    database.savedAddressDao.addAddress(address)

            } catch (e: Exception) {
                //Saving the address for the purpose of caching is not an important function
                // In case there is an error, app will absorb it here
                e.printStackTrace()
            }
        }
    }

    suspend fun retrieveSavedAddress(): Address {
        Log.i(TAG_Repo1, "inside retrieveSavedAddress")
        var address = Address("","","","","")
        withContext(Dispatchers.IO) {
            Log.i(TAG_Repo1, "inside  the retrieveSavedAddress-Dispatcher.IO")
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

private fun getRepresentativesProfile(representativeResponse : RepresentativeResponse): List<RepresentativeProfile> {
    Log.i(TAG_Repo1, "Inside getRepresentativesProfile")

    val representativeProfileArray = ArrayList<RepresentativeProfile>()

    val officials = representativeResponse.officials
    val offices = representativeResponse.offices

    var id: Int =0
    offices.forEach {
        val representatives = it.getRepresentatives(officials)

        representatives.forEach {
            val official = it.official
            val office = it.office
            val titleName = office.name
            val name = official.name
            var profileImageURL = ""
            if (official.photoUrl != null) {
                profileImageURL = official.photoUrl
            }
            val party = official.party
            var channels = emptyList<Channel>()
            if (official.channels != null ) {
                channels = official.channels
            }

            val repProfile = RepresentativeProfile(id, profileImageURL!!,titleName,name, party!!, channels!!)
            representativeProfileArray.add(repProfile)
            id++
        }
        Log.i(TAG_Repo1, "Check point # 10")
    }
    Log.i(TAG_Repo1, "List of Representative Profile:${representativeProfileArray.size}")
    return representativeProfileArray.toList()
}


fun loadTestRepreentativeProfile(application: Application): String {
    Log.i(TAG_Repo1, "Inside loadTestRepreentativeProfile")

    val file = File(application.filesDir, "/representativeprofileOK.json")
    val representativeProfileJsonString: String = file.readText(Charsets.UTF_8)
    Log.i(TAG_Repo1, representativeProfileJsonString)

    return  representativeProfileJsonString
}

fun getCiviRepAPIRepresentativeResponse(input: JSONObject) : RepresentativeResponse {
    Log.i(TAG_Repo1, "Inside getCiviRepAPIRepresentativeResponse")

    //Extract the Office objects from the JSON
//    var officeJsonObjects = input.getJSONObject("offices")
   // val officeJsonArray = officeJsonObjects.getJSONArray("offices")
    val officeJsonArray = input.getJSONArray("offices")
    val officeArray = ArrayList<Office>()

    Log.i(TAG_Repo1, "Before Office For-Loop")
    for (i in 0 until officeJsonArray.length()) {
        val officeJson = officeJsonArray.getJSONObject(i)
        val officeTitle = officeJson.getString("name")
        val divisionId = officeJson.getString("divisionId")
        val adapter = ElectionAdapter()
        val division = adapter.divisionFromJson(divisionId)
        val officialIndices = officeJson.getJSONArray("officialIndices")
        Log.i(TAG_Repo1, "officialIndices array size:${officialIndices.length()}")
        Log.i(TAG_Repo1, "officialIndices:${officialIndices.toString()}")
        var officialIndicesArray = arrayListOf<Int>()
        for (j in 0 until officialIndices.length()) {
            Log.i(TAG_Repo1, "Inside the for loop to extract official index int-array:[j]=${j}")
            val officialIndex = officialIndices.getInt(j)
            officialIndicesArray.add(officialIndex)
        }

        val office = Office(officeTitle,division, officialIndicesArray.toList())
        officeArray.add(office)
    }

    val officeList = officeArray.toList()
    Log.i(TAG_Repo1, "Size of the Office-List:${officeList.size}")

    //Extrat the Official objects from the JSON
//    var officialJsonObjects = input.getJSONObject("officials")
    val officialArray = ArrayList<Official>()
//    val officialJsonArray = officialJsonObjects.getJSONArray("officials")
    val officialJsonArray = input.getJSONArray("officials")

    Log.i(TAG_Repo1, "Before Official For-Loop")
    for (i in 0 until officialJsonArray.length()) {
        val officialJson = officialJsonArray.getJSONObject(i)
        val repName = officialJson.getString("name")
        val repParty = officialJson.getString("party")
        var profileImageURL: String = null.toString()
        try {
            profileImageURL = officialJson.getString("photoUrl")
        } catch (e: Exception){
            Log.i(TAG_RVM, "No value for photoURL")
        }

        var channelsJsonArray = JSONArray()
        try {
            channelsJsonArray = officialJson.getJSONArray("channels")
        } catch (e: Exception){
            Log.i(TAG_RVM, "No value for channels")
        }

        var channelsArray = arrayListOf<Channel>()

        for (k in 0 until channelsJsonArray.length()) {
            val channel = channelsJsonArray.getJSONObject(k)
            val channelType = channel.getString("type")
            val channelId = channel.getString("id")
            channelsArray.add(Channel(channelType,channelId))
        }
        officialArray.add(Official(repName,null,repParty,
                            null,null,profileImageURL,channelsArray.toList()))
    }

    val officialList = officialArray.toList()
    Log.i(TAG_Repo1, "Size of the Officials-List:${officialList.size}")

    val representativeResponse = RepresentativeResponse(officeList, officialList)
    return representativeResponse
}

