package com.example.android.politicalpreparedness.election

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.network.models.Election
import java.lang.IllegalArgumentException

class VoterInfoViewModelFactory(private val selectedElection : Election,
                                private val dataSource: ElectionDao,
                                private val application: Application): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(VoterInfoViewModel::class.java)) {
            return VoterInfoViewModel (selectedElection,dataSource, application) as T
        }
        throw IllegalArgumentException ("Unknown ViewModel class")
    }

}