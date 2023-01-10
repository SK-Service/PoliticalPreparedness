package com.example.android.politicalpreparedness.database

import androidx.room.*
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.SavedElection

//The Data Access Object pattern - an interface to retrieve or insert data
@Dao
interface ElectionDao {

    //TODO: Add insert query
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg elections: Election)

    //TODO: Add select all election query
    @Query ("select * from election_table")
    fun getElectionList() : List<Election>

    //TODO: Update isSave on Election Entity
//    @Query("UPDATE election_table SET isSaved = :isSaved WHERE id = :id")
//    fun  updateElectionFollowingStatus(isSaved: Boolean, id: Int)

    //TODO: Add select single election query

    //TODO: Add delete query

    //TODO: Add clear query

}

@Dao
interface SavedElectionDao {

    //TODO: Add insert query
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addSavedElection(elections: SavedElection)

    @Delete
    fun deleteSavedElection(elections: SavedElection)

    @Query ("select * from saved_election_table")
    fun getSavedElectionList() : List<SavedElection>

    @Query("select * from saved_election_table where id = :selectedElectionID")
    fun getSavedElection(selectedElectionID: Int) : SavedElection


}

@Dao
interface SavedAddressDao {

    //TODO: Add insert query
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addAddress(address: Address)

    //Only one address will be saved in the Address table
    @Query ("select * from address_table LIMIT 1")
    fun getAddress() : Address

    @Query("delete from address_table")
    fun removeAllAddress()

}