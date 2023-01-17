package com.example.android.politicalpreparedness.election

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.representative.RepresentativeViewModel
import java.lang.IllegalArgumentException

//TODO: Create Factory to generate VoterInfoViewModel with provided election datasource
class RegisteredAddressViewModelFactory(private val dataSource: ElectionDao,
                                        private val application: Application): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(RegisteredAddressViewModel::class.java)) {
            return RegisteredAddressViewModel (dataSource, application) as T
        }
        throw IllegalArgumentException ("Unknown ViewModel class")
    }

}
