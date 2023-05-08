package org.xluz.droidacts.drangenotes

import androidx.room.Dao
import androidx.room.Query

@Dao
interface CCgolfDBDao {
    @Query("SELECT * FROM Golfers ORDER BY id")
    fun getGolfers(): List<Golferdata>
    @Query("SELECT name FROM Golfers ORDER BY id")
    fun getGolfersname(): List<String>
}