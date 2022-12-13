package com.example.android.politicalpreparedness.election

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.network.models.Election

class VoterInfoViewModel(private val dataSource: ElectionDao) : ViewModel() {

    //TODO: Add live data to hold voter info
//    //To hold the selected Asteroid through app lifecycle
//    private val _selectedElection = MutableLiveData<Election>()
//    val selectedElection: LiveData<Election>
//        get() = _selectedElection
//
//    init {
//        _selectedElection.value = asteroid
//    }

    //TODO: Add var and methods to populate voter info

    //TODO: Add var and methods to support loading URLs

    //TODO: Add var and methods to save and remove elections to local database
    //TODO: cont'd -- Populate initial state of save button to reflect proper action based on election saved status

    /**
     * Hint: The saved state can be accomplished in multiple ways. It is directly related to how elections are saved/removed from the database.
     */

}