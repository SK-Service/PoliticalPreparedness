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

enum class RepresentativeCivicApiStatus {LOADING, ERROR, ERROR_404, DONE }
const val TAG_RVM = "RepresentativeViewModel"
class RepresentativeViewModel(datasource: ElectionDao, application: Application):
    AndroidViewModel(application) {

    val application1 = application

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

    //Manage location address retrieval complete - false indicates location retrieval is not complete
    private var _locationAddressRetrievalCompleteFlag = MutableLiveData<Boolean>(false)
    val locationAddressRetrievalCompleteFlag: LiveData<Boolean>
        get() = _locationAddressRetrievalCompleteFlag

    //Manage location services and app location usage permission -
    // false indicates permission is not granted
    private var _locationPermissionGranted = MutableLiveData<Boolean>(false)
    val locationPermissionGranted: LiveData<Boolean>
        get() = _locationPermissionGranted

    //Manage button flag to distinguish between which button is selected
    private var _repButtonClickedType = MutableLiveData<String>()
    val repButtonClickedType: LiveData<String>
        get() = _repButtonClickedType

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

    fun searchMyRepresentative(address: Address) {
        Log.i(TAG_RVM, "inside searchMyRepresentative")

        Log.i(TAG_RVM, "Address: ${address.toFormattedString()}")
        viewModelScope.launch {
            var repList: List<RepresentativeProfile>
            _civicRepAPICallStatus.value = RepresentativeCivicApiStatus.LOADING
            try {
                    //Reset address entered check flag to true, which is the initial state
                    _addressEnteredFlag.value = true
                    repList = RepresentativeRepo(getApplication(), ElectionDatabase.getInstance(application1))
                        .refreshRepresentativeList(address)
                    _listOfRepresentatives.value = repList.toMutableList()
                    Log.i(TAG_RVM, "Size of _listOfRepresentatives.value: " +
                                    "${_listOfRepresentatives.value!!.size}")

                    //Consider it a valid address if list of representatives are returned

                Log.i(TAG_RVM, "Representative List ${_listOfRepresentatives.value?.size}")

                _civicRepAPICallStatus.value = RepresentativeCivicApiStatus.DONE
            } catch (e: Exception){
                Log.i(TAG_RVM, "After RepresentativeRepo call and inside catch exception")
                e.printStackTrace()
                if (e.message?.contains("404") == true) {
                      _civicRepAPICallStatus.value = RepresentativeCivicApiStatus.ERROR_404
                } else {
                    _civicRepAPICallStatus.value = RepresentativeCivicApiStatus.ERROR
                }
            }

        }
    }

    fun saveAddress(address: Address) {
        viewModelScope.launch {
            Log.i(TAG_RVM, "inside saveAddress")
            _address.value = address
            RepresentativeRepo(getApplication(),
                ElectionDatabase.getInstance(application1)).saveAddress( address)
        }
    }

    private suspend fun retrieveSavedAddress(): Address {
        Log.i(TAG_RVM, "inside retrieveSavedAddress")
        val address = RepresentativeRepo(getApplication(),
            ElectionDatabase.getInstance(application1)).retrieveSavedAddress()

        return  address
    }

    fun checkAddressGoodForSearch(address: Address) : Boolean {
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

    fun locationAddressRetrievalComplete() {
        _locationAddressRetrievalCompleteFlag.value = true
    }

    fun resetLocationAddressRetrievalComplete() {
        _locationAddressRetrievalCompleteFlag.value = false
    }

    fun resetRepresentativeList() {
        _listOfRepresentatives.value = mutableListOf<RepresentativeProfile>()
    }

    fun addressIsIncomplete() {
        _addressEnteredFlag.value = false
    }

    fun resetAddressIsIncomplete() {
        _addressEnteredFlag.value = true
    }

    fun setAddressCacheAvailableFlag() {
        _addressCacheAvailableFlag.value = true
    }

    fun resetAddressCacheAvailableFlag() {
        _addressCacheAvailableFlag.value = false
    }

    fun locationPermissionGranted() {
        _locationPermissionGranted.value = true
    }

    fun locationPermissionNotGranted() {
        _locationPermissionGranted.value = false
    }

    fun setRepButtonClickedTypeToManual()  {
        _repButtonClickedType.value = "MANUAL"
    }

    fun setRepButtonClickedTypeToLocation()  {
        _repButtonClickedType.value = "LOCATION"
    }

}