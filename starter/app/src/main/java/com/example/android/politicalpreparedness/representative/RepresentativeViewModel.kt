package com.example.android.politicalpreparedness.representative

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.election.ElectionCivicApiStatus
import com.example.android.politicalpreparedness.election.TAG
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.repository.ElectionRepo
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

    //Manage address cache  Flag - false indicates no cache address is available
    private var _addressCacheAvailableFlag = MutableLiveData<Boolean>(false)
    val addressCacheAvailableFlag: LiveData<Boolean>
        get() = _addressCacheAvailableFlag

    init {
        Log.i(TAG_RVM, "inside the Representative ViewModel init")
        viewModelScope.launch {
            try {
                val address = retrieveSavedAddress()
                if (address != null) {
                    if(!address.line1.isNullOrEmpty() || !address.city.isNullOrEmpty() ||
                        !address.zip.isNullOrEmpty()) {
                        _address.value = address
                        //Set the flag to true to indicate that there is cached value
                        _addressCacheAvailableFlag.value = true
                    }
                }
            } catch (e: Exception){
                Log.i(TAG, "After database retrieve failure")
                e.printStackTrace()
            }
        }
    }

    fun searchMyRepresentative(line1:String, line2: String, city: String,
                               state: String, zip:String) {
        Log.i(TAG_RVM, "inside searchMyRepresentative")

        val address = Address(line1, line2, city ,state,zip)
        Log.i(TAG_RVM, "Address: ${address.toFormattedString()}")
        viewModelScope.launch {
            var repList: List<RepresentativeProfile>
            _civicRepAPICallStatus.value = RepresentativeCivicApiStatus.LOADING
            try {
                Log.i(TAG_RVM, "Address Validity:${checkAddressGoodForSearch(address)}")
                if (checkAddressGoodForSearch(address)) {
                    //Reset address entered check flag to true, which is the initial state
                    _addressEnteredFlag.value = true
                    repList = RepresentativeRepo(getApplication(), ElectionDatabase.getInstance(application1))
                        .refreshRepresentativeList(address)
                    _listOfRepresentatives.value = repList.toMutableList()
                    Log.i(TAG_RVM, "Size of _listOfRepresentatives.value: " +
                                    "${_listOfRepresentatives.value!!.size}")

                    //TODO - Save the address in a table and also add the address to the LiveData
                    _address.value = address
                    //Cache the address for future use
                    saveAddress()

                } else {
                    //addressEnteredFlag value is false when button is clicked without zip
                        // or address line 1 or city
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

    private suspend fun saveAddress() {
        Log.i(TAG_RVM, "inside saveAddress")
        RepresentativeRepo(getApplication(), ElectionDatabase.getInstance(application1)).saveAddress(
            _address.value!!
        )
    }

    private suspend fun retrieveSavedAddress(): Address {
        Log.i(TAG_RVM, "inside retrieveSavedAddress")
        val address = RepresentativeRepo(getApplication(),
            ElectionDatabase.getInstance(application1)).retrieveSavedAddress()

        return  address
    }

    private fun checkAddressGoodForSearch(address: Address) : Boolean {
        var addressValidity = false
        Log.i(TAG_RVM, "Address Line1:<${address.line1}>")
        Log.i(TAG_RVM, "City1:<${address.city}>")
        Log.i(TAG_RVM, "zip:<${address.zip}>")

        if (checkCityValidity(address.city) || checkAddressLine1Validity(address.line1) ||
                checkUSAZipValidity(address.zip)) {
            addressValidity = true
        }
        return addressValidity
    }

    private fun checkUSAZipValidity(zip: String): Boolean {
        var zipValidity = false
        if (!zip.isNullOrEmpty()) {
            zip.trimStart()
            zip.trimEnd()
            if (zip.length == 5) {
                if (zip.toDoubleOrNull() != null) {
                    zipValidity = true
                }
            }
        }
        return zipValidity
    }

    private fun checkCityValidity(city: String) : Boolean {
        var cityValidity = false
        if (!city.isNullOrEmpty()) {
            city.trimStart()
            city.trimEnd()
            if(city.length >= 3) {
                cityValidity = true
            }
        }
        return cityValidity
    }

    private fun checkAddressLine1Validity(line1: String) : Boolean {
        var addressValidity = false
        Log.i(TAG_RVM, "Address Line1:<${line1}>")
        if(!line1.isNullOrEmpty()) {
            Log.i(TAG_RVM, "Address line 1 is not null")
          line1.trimEnd()
           line1.trimStart()
            if (!line1.isEmpty() || line1 != "\n") {
                Log.i(TAG_RVM, "Address line 1 is not empty or new line")
                addressValidity = true
            }
        }
        return addressValidity
    }

    fun resetRepCivicAPICallStatusToDone() {
        _civicRepAPICallStatus.value = RepresentativeCivicApiStatus.DONE
    }
}