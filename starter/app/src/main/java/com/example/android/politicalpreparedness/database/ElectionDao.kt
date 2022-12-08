package com.example.android.politicalpreparedness.database

import androidx.room.*
import com.example.android.politicalpreparedness.network.models.Election

//The Data Access Object pattern - an interface to retrieve or insert data
@Dao
interface ElectionDao {

    //TODO: Add insert query
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg elections: Election)

    //TODO: Add select all election query
    @Query ("select * from election_table")
    fun getElectionList() : List<Election>

    //TODO: Add select single election query

    //TODO: Add delete query

    //TODO: Add clear query

}