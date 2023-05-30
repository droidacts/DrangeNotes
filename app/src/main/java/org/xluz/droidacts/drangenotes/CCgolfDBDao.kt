package org.xluz.droidacts.drangenotes

import androidx.room.*

@Dao
interface CCgolfDBDao {
//    @Query("PRAGMA wal_checkpoint(FULL)")
//    suspend fun flushWAL(): List<Int>
    @Query("SELECT * FROM Golfers ORDER BY id")
    suspend fun getGolfers(): List<Golferdata>

    @Query("SELECT name FROM Golfers ORDER BY id")
    fun getGolfersname(): List<String>

    @Insert
    suspend fun logOneshot(shot: Shotdata)

    @Query("SELECT COUNT(*) FROM Shots")
    fun getNShots(): Int
    @Query("SELECT * from Shots ORDER BY datetime")
    suspend fun getALLShots(): List<Shotdata>

    @Query("SELECT * from Shots ORDER BY datetime DESC LIMIT 20")
    suspend fun getLast20Shots(): List<Shotdata>
    @Query("SELECT * from Shots WHERE datetime BETWEEN :timenow-86400 AND :timenow ORDER BY datetime")
    suspend fun getRecentdayShots(timenow: Int): List<Shotdata>

}