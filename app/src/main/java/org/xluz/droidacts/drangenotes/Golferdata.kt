package org.xluz.droidacts.drangenotes

import androidx.room.*

@Entity(tableName = "Golfers")
data class Golferdata(
    @PrimaryKey(autoGenerate=true) val id: Int,
    val name: String,
    val info: String?
)

