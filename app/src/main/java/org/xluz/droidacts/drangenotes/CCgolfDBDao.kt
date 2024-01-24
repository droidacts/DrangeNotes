package org.xluz.droidacts.drangenotes
/**
The interface between in-code data structures and the room (sqlite) database
Copyright(C) 2024 by Cecil Cheung PhD

This source code file is released under GNU General Public License version 3.
See www.gnu.org/licenses/gpl-3.0.html
 */
import androidx.room.*

@Dao
interface CCgolfDBDao {
    @Query("SELECT * FROM Golfers ORDER BY id")
    fun getGolfers(): List<Golferdata>

    @Query("SELECT title FROM Sticks ORDER BY id LIMIT 14")
    fun getSticksDefault(): List<String>

    @Insert
    suspend fun logOneshot(shot: Shotdata)

    @Query("SELECT COUNT(*) FROM Shots")
    fun getNShots(): Int
    @Query("SELECT * from Shots ORDER BY datetime")
    suspend fun getALLShots(): List<Shotdata>
    @Query("SELECT * from Shots WHERE golfer=:player ORDER BY datetime")
    suspend fun getALLShotsBy(player: Int): List<Shotdata>
    @Query("SELECT * from Shots ORDER BY datetime DESC LIMIT :n")
    suspend fun getLastNShots(n: Int): List<Shotdata>
    @Query("SELECT * from Shots WHERE golfer=:player ORDER BY datetime DESC LIMIT :n")
    suspend fun getLastNShotsBy(n: Int, player: Int): List<Shotdata>
    @Query("SELECT * from Shots WHERE datetime BETWEEN :timenow-86400 AND :timenow ORDER BY datetime")
    suspend fun getRecentdayShots(timenow: Long): List<Shotdata>
    @Query("SELECT * from Shots WHERE (golfer=:player) and (datetime BETWEEN :timenow-86400 AND :timenow) ORDER BY datetime")
    suspend fun getRecentdayShotsBy(timenow: Long, player: Int): List<Shotdata>
    @Query("SELECT * from Shots WHERE (golfer=:player) and (datetime > :ttime) ORDER BY datetime DESC LIMIT :n")
    suspend fun getLastNShotsBySince(n: Int, player: Int, ttime: Long,): List<Shotdata>
}