package com.example.android.politicalpreparedness.representative

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.network.models.Election
import java.lang.IllegalArgumentException

//TODO: Create Factory to generate VoterInfoViewModel with provided election datasource
class RepresentativeViewModelFactory(private val dataSource: ElectionDao,
                                     private val application: Application): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(RepresentativeViewModel::class.java)) {
            return RepresentativeViewModel (dataSource, application) as T
        }
        throw IllegalArgumentException ("Unknown ViewModel class")
    }

}