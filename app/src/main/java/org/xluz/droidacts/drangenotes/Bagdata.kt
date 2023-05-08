package org.xluz.droidacts.drangenotes

import androidx.room.*

@Entity(tableName = "Bags")
data class Bagdata(
    @PrimaryKey val id: Int,
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
