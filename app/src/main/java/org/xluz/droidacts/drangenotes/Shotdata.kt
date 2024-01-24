package org.xluz.droidacts.drangenotes
/**
Definition of a data class to represent a SQL table access (generate) thro room database
Copyright(C) 2024 by Cecil Cheung PhD

This source code file is released under GNU General Public License version 3.
See www.gnu.org/licenses/gpl-3.0.html
 */
import androidx.room.*
import java.util.*
import java.text.SimpleDateFormat

// altering this data class will cause a database migration
@Entity(
    tableName = "Shots",
    foreignKeys = [
        ForeignKey(entity = Stickdata::class, parentColumns = ["id"], childColumns = ["stick"]),
        ForeignKey(entity = Golferdata::class, parentColumns = ["id"], childColumns = ["golfer"]),
        ForeignKey(entity = Pleveldata::class, parentColumns = ["idx"], childColumns = ["power"]),
        ForeignKey(entity = Trajdata::class, parentColumns = ["type"], childColumns = ["sshape"])
    ]
)
data class Shotdata(
    var dist: Float = 0.0f ,        //col 2
    var power: Int? = 0 ,           //col 3
    var stick: Int = 0 ,            //col 4
    var sshape: Int? = 0 ,          //col 5
    var golfer : Int? = 0 ,         //col 6
    var loc: String? = "" ,         //col 8
    var comment: String? = ""       //col 9
)
{
    @PrimaryKey(autoGenerate = true)
    var sn: Int = 0                //col 1, probably not used, should be filled/autoincrement by sqlite
    var datetime: Int? = null      //col 7, in unix time
    fun settimeStamp(): String {
        datetime = ((Date().time)/1000).toInt()
        return Date(datetime!! *1000L).toString()
    }
    override fun toString(): String {
        // use in logs
        val dtformat = SimpleDateFormat("HH:mm", Locale.US)
        var outstr = dtformat.format(Date(1000L* datetime!!))
        outstr += " [$golfer] club $stick at $power >> $dist yds ($sshape)"
        outstr += " { $comment ; $loc }"
        return outstr
    }
}