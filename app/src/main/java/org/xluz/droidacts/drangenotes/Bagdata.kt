package org.xluz.droidacts.drangenotes

import androidx.room.*

@Entity(
    tableName = "Bags",
    foreignKeys = [
        ForeignKey(entity=Stickdata::class, parentColumns=["id"], childColumns=["s01"]),
        ForeignKey(entity=Golferdata::class, parentColumns=["id"], childColumns=["golfer"])
    ]
)
data class Bagdata(
    @PrimaryKey(autoGenerate=true) val id: Int,
    @ColumnInfo(defaultValue = "0")
    val golfer: Int = 0,    //0 is valid (1st) row in Golfers table
    val desc: String?,
    val s01: Int,
    val s02: Int?,
    val s03: Int?,
    val s04: Int?,
    val s05: Int?,
    val s06: Int?,
    val s07: Int?,
    val s08: Int?,
    val s09: Int?,
    val s10: Int?,
    val s11: Int?,
    val s12: Int?,
    val s13: Int?,
    val s14: Int?
)
