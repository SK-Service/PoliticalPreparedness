package com.example.android.politicalpreparedness.election

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.VoterInfo
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import com.example.android.politicalpreparedness.repository.ElectionRepo
import com.example.android.politicalpreparedness.repository.RepresentativeRepo
import com.example.android.politicalpreparedness.repository.VoterInfoRepo
import com.example.android.politicalpreparedness.representative.TAG_RVM
import kotlinx.coroutines.launch
import java.lang.Appendable
import java.util.*

const val TAG5 = "RegisteredAddressViewModel"

class RegisteredAddressViewModel(dataSource: ElectionDao,
                                application: Application) : AndroidViewModel(application) {
    val application1 = application
    //Managed data for API retrieval status
    private var _registeredAddress = MutableLiveData<Address>()
    val registeredAddress: LiveData<Address>
        get() = _registeredAddress

    //Manage address cache  Flag - false indicates no cache address is available
    private var _addressCacheAvailableFlag = MutableLiveData<Boolean>(false)
    val addressCacheAvailableFlag: LiveData<Boolean>
        get() = _addressCacheAvailableFlag

    //Manage addressEntered Flag
    private var _addressEnteredFlag = MutableLiveData<Boolean>(true)
    val addressEnteredFlag: LiveData<Boolean>
        get() = _addressEnteredFlag

    //Whether address is save Flag
    private var _addressIsSavedInDB = MutableLiveData<Boolean>(false)
    val addressIsSavedInDB: LiveData<Boolean>
        get() = _addressIsSavedInDB

    var address: Address? = Address("","","","","")
    init {
        Log.i(TAG1, "inside init - where VoterInfo data is fetched from google api")

        viewModelScope.launch {
            try {
                    address= VoterInfoRepo(ElectionDatabase.getInstance(application))
                    .retrieveAddress()
                    if (address != null ) {
                        _registeredAddress.value = address
                    }
            } catch (e: Exception){
                Log.i(TAG1, "After VoterInfoRepo call and inside catch exception")
                e.printStackTrace()

            }
        }
    }
    fun addressIsIncomplete() {
        _addressEnteredFlag.value = false
    }

    fun resetAddressIsIncomplete() {
        _addressEnteredFlag.value = true
    }

    fun saveAddress(address: Address) {
        viewModelScope.launch {
            Log.i(TAG_RVM, "inside saveAddress")
            _registeredAddress.value = address
            RepresentativeRepo(getApplication(),
                ElectionDatabase.getInstance(application1)).saveAddress( address)
        }
    }

    fun isAddressSavedInDB() : Boolean? {
        return addressIsSavedInDB.value
    }

    fun setAddressSavedInDB(){
        _addressIsSavedInDB.value = true
    }

    fun resetAddressSavedInDB(){
        _addressIsSavedInDB.value = false
    }
}


