package org.xluz.droidacts.drangenotes

import androidx.room.*

@Entity(tableName = "Sticks")
data class Stickdata(
    @PrimaryKey val id: Int,
    val title: String,
    val desc: String?,
    val specs: String?
)
