package org.xluz.droidacts.drangenotes

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities=[Shotdata::class, Golferdata::class, Pleveldata::class, Stickdata::class, Trajdata::class, Bagdata::class],
    version=2, exportSchema=false)
abstract class CCgolfDB: RoomDatabase() {
    abstract fun theDAO(): CCgolfDBDao
}