package com.example.android.politicalpreparedness.database

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.SavedElection

@Database(entities = [Election::class, SavedElection::class, Address::class], version = 3, exportSchema = false)
@TypeConverters(Converters::class)
abstract class ElectionDatabase: RoomDatabase() {

    abstract val electionDao: ElectionDao
    abstract val savedElectionDao : SavedElectionDao
    abstract val savedAddressDao: SavedAddressDao

    companion object {

        @Volatile
        private var INSTANCE: ElectionDatabase? = null

        fun getInstance(context: Context): ElectionDatabase {
            synchronized(this) {
                Log.i("ElectionDatabase","get hold of the DB instance")
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                            context.applicationContext,
                            ElectionDatabase::class.java,
                            "election_database"
                    )
                            .fallbackToDestructiveMigration()
                            .build()

                    INSTANCE = instance
                }

                return instance
            }
        }

    }

}