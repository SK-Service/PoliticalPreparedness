package com.example.android.politicalpreparedness.repository

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

const val TAG_Repo = "ElectionRepo"
class RepresentativeRepo (private val database: ElectionDatabase) {
//    private lateinit var electionListDB: List<Election>
//    private lateinit var savedElectionListDB: List<SavedElection>
    private lateinit var representativeLists: List<RepresentativeProfile>

    suspend fun refreshRepresentativeList (address: Address): List<RepresentativeProfile> {
        Log.i(TAG_Repo, "inside refreshRepresentativeList")
        withContext(Dispatchers.IO) {
            Log.i(TAG, "inside  the refreshRepresentativeList-Dispatcher.IO")

            try {
                Log.i(TAG, "Before call to Civic Representative API")
                //TODO - This is has to be further anlyzed to ensure that the complex Json object is dealt with
//                val representativeListFromCivic = CivicsApi.electionRetrofitService.
//                                getCivicRepresentatives(address.toFormattedString(),
//                                            true)
//                Log.i(TAG, "After the Civic API call")
//                Log.i(TAG, "RepresentativeOfficesLength:${representativeListFromCivic.offices.size}")
//                Log.i(TAG, "No Database insert exist yet")

                //TODO - TO BE DELETED - HARD CODED JSON
                val repProfileJsonString: String = loadTestRepreentativeProfile()
                val representativeListFromCivic = getCiviRepAPIRepresentativeResponse(
                                                    JSONObject(repProfileJsonString))

//                Log.i("TAG_Repo", "After call to Civic Rep API: Length of return:" +
//                        "                               <${asteroidListNasa.length}>")
//                Log.i("AsteroidRepo", "Before parsing the JSON String")
//                val asteroidListNasa_arraylist = parseAsteroidsJsonResult(JSONObject(asteroidListNasa))
//                Log.i("AsteroidRepo", "After parsing the JSON String-" +
//                        "Number of Asteroids:<${asteroidListNasa_arraylist.size}>")

                //Response has got offices and for each office,there are one ore more official
                //object
                representativeLists = getRepresentativesProfile (representativeListFromCivic)
            } catch (e: Exception) {
                //HTTP Exception are not reflected back to the caller
                // Any other system error the caller should handle
                e.printStackTrace()
            }

        }
        return representativeLists
    }


}
//TODO - This function needs to be built
private fun getRepresentativesProfile(representativeResponse : RepresentativeResponse): List<RepresentativeProfile> {
    Log.i(TAG_Repo, "Inside getRepresentativesProfile")
    var representativeProfile:RepresentativeProfile =
        RepresentativeProfile(1,"", "", "","",
                    listOf(Channel("","")))
    return listOf(representativeProfile)
}


fun loadTestRepreentativeProfile(): String {
    Log.i(TAG_Repo, "Inside loadTestRepreentativeProfile")
//    val representativeProfileJsonString: String =
//        File("C:\\cde\\ktln\\ndgre\\PoliticalPrep" +
//                "\\poliprep\\starter\\app\\src\\main\\res\\representativeprofile.json").
//                                readText(Charsets.UTF_8)
    val representativeProfileJsonString: String =
        File("C:/cde/ktln/ndgre/PoliticalPrep" +
                "/poliprep/starter/app/src/main/res/representativeprofile.json").
                                        readText(Charsets.UTF_8)

    return  representativeProfileJsonString
}

fun getCiviRepAPIRepresentativeResponse(input: JSONObject) : RepresentativeResponse {
    Log.i(TAG_Repo, "Inside getCiviRepAPIRepresentativeResponse")

    //Extract the Office objects from the JSON
    var officeJsonObjects = input.getJSONObject("offices")
    val officeJsonArray = officeJsonObjects.getJSONArray("offices")
    val officeArray = ArrayList<Office>()

    Log.i(TAG_Repo, "Before Office For-Loop")
    for (i in 0 until officeJsonArray.length()) {
        val officeJson = officeJsonArray.getJSONObject(i)
        val officeTitle = officeJson.getString("name")
        val divisionId = officeJson.getString("divisionId")

        val officialIndices = officeJson.getString("officialIndices")
        Log.i(TAG_Repo, "officialIndices:${officialIndices}")

        val office = Office(officeTitle,Division("","",""), listOf(1,2,3,4,5))
        officeArray.add(office)
    }
    val officeList = officeArray.toList()
    Log.i(TAG_Repo, "Size of the OfficeList:${officeList.size}")

    //Extrat the Official objects from the JSON
    var officialJsonObjects = input.getJSONObject("officials")
    val officialArray = ArrayList<Official>()
    val officialJsonArray = officialJsonObjects.getJSONArray("officials")

    Log.i(TAG_Repo, "Before Official For-Loop")
    for (i in 0 until officialJsonArray.length()) {
        val officialJson = officialJsonArray.getJSONObject(i)
        val repName = officialJson.getString("name")
        val repParty = officialJson.getString("party")

        val official = Official(repName)
        officialArray.add(official)
    }

    val officialList = officialArray.toList()
    Log.i(TAG_Repo, "Size of the OfficialsList:${officialList.size}")


    val representativeResponse = RepresentativeResponse(officeList, officialList)
    return representativeResponse
}

