package org.xluz.droidacts.drangenotes

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities=[Shotdata::class, Golferdata::class, Pleveldata::class, Stickdata::class, Trajdata::class, Bagdata::class],
    version=3, exportSchema=false)
abstract class CCgolfDB: RoomDatabase() {
    abstract fun theDAO(): CCgolfDBDao

    companion object {
        // singleton
        private var oneINSTANCE: CCgolfDB? = null
        fun get(context: Context): CCgolfDB {
            if(oneINSTANCE==null) {
                oneINSTANCE = Room.databaseBuilder(context.applicationContext, CCgolfDB::class.java,
                "CCgolf.db")
                    .createFromAsset("roomCCgolf.db")
                    .allowMainThreadQueries()    //for testing
                    .build()
            }
            return oneINSTANCE as CCgolfDB
        }

        fun getOne(): CCgolfDB? {
            return oneINSTANCE
        }
    }
}