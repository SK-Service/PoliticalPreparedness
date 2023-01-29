package com.example.android.politicalpreparedness.election

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.database.ElectionDao
import java.lang.IllegalArgumentException

class ElectionsViewModelFactory ( private val datasource: ElectionDao,
                                  private val application: Application):
                                                        ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(ElectionsViewModel::class.java)) {
            return ElectionsViewModel (datasource, application) as T
        }
        throw IllegalArgumentException ("Unknown ViewModel class")
    }

}
