package org.xluz.droidacts.drangenotes

import androidx.room.*

@Entity(tableName = "Plvl")
data class Pleveldata(
    @PrimaryKey val idx: Int,
    val desc: String?
)
