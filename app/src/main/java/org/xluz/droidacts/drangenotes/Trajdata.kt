package org.xluz.droidacts.drangenotes

import androidx.room.*

@Entity(tableName = "Traj")
data class Trajdata(
    @PrimaryKey val type: Int,
    val desc: String?,
    val icon: Int?
)
