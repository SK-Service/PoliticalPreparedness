package com.example.android.politicalpreparedness.representative

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.repository.RepresentativeRepo
import com.example.android.politicalpreparedness.representative.model.RepresentativeProfile
import kotlinx.coroutines.launch

enum class RepresentativeCivicApiStatus {LOADING, ERROR, DONE }
const val TAG_RVM = "RepresentativeViewModel"
class RepresentativeViewModel(datasource: ElectionDao, application: Application):
    AndroidViewModel(application) {

    val application1 = application
    //TODO: Create live data val for upcoming elections
    //Managed data for list of elections
    private var _listOfRepresentatives = MutableLiveData<MutableList<RepresentativeProfile>>(mutableListOf())
    val listOfRepresentatives: LiveData<MutableList<RepresentativeProfile>>
        get() = _listOfRepresentatives

    //Managed data for API retrieval status
    private var _civicRepAPICallStatus = MutableLiveData<RepresentativeCivicApiStatus>()
    val civicRepAPICallStatus: LiveData<RepresentativeCivicApiStatus>
        get() = _civicRepAPICallStatus

    //Managed addressed input
    private var _address = MutableLiveData<Address>()
    val address: LiveData<Address>
        get() = _address

    //Manage addressEntered Flag
    private var _addressEnteredFlag = MutableLiveData<Boolean>(true)
    val addressEnteredFlag: LiveData<Boolean>
        get() = _addressEnteredFlag


    fun checkAddressGoodForSearch(address: Address) : Boolean {
        var addressValidity = false
        if(address.line1 != null) {
            address.line1.trimEnd()
            address.line1.trimStart()
            if (address.line1 != "" || address.line1 != "\n") {
                addressValidity = true
            }
        }
        return addressValidity
    }

    fun searchMyRepresentative(line1:String, line2: String, city: String,
                               state: String, zip:String) {
        Log.i(TAG_RVM, "inside searchMyRepresentative")
//        val line1 = "Amphitheatre Parkway"
//        val line2 = "1600"
//        val address = Address(line1, line2, "Mountain View" ,"California","94043")
        val address = Address(line1, line2, city ,state,zip)
        Log.i(TAG_RVM, "Address: ${address.toFormattedString()}")
        viewModelScope.launch {
            var repList: List<RepresentativeProfile>
            _civicRepAPICallStatus.value = RepresentativeCivicApiStatus.LOADING
            try {
                if (checkAddressGoodForSearch(address)) {
                    //Reset address entered check flag to true, which is the initial state
                    _addressEnteredFlag.value = true
                    repList = RepresentativeRepo(getApplication(), ElectionDatabase.getInstance(application1))
                        .refreshRepresentativeList(address)
                    _listOfRepresentatives.value = repList.toMutableList()
                    Log.i(TAG_RVM, "Size of _listOfRepresentatives.value: " +
                                    "${_listOfRepresentatives.value!!.size}")
                } else {
                    //addressEnteredFlag value is false when button is clicked without address line 1
                    _addressEnteredFlag.value = false
                }

                Log.i(TAG_RVM, "Representative List ${_listOfRepresentatives.value?.size}")

                _civicRepAPICallStatus.value = RepresentativeCivicApiStatus.DONE
            } catch (e: Exception){
                Log.i(TAG_RVM, "After RepresentativeRepo call and inside catch exception")
                e.printStackTrace()
                _civicRepAPICallStatus.value = RepresentativeCivicApiStatus.ERROR
            }

        }
    }


}