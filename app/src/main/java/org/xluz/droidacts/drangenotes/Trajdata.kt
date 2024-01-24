package org.xluz.droidacts.drangenotes
/**
Definition of a data class to represent a SQL table access (generate) thro room database
Copyright(C) 2024 by Cecil Cheung PhD

This source code file is released under GNU General Public License version 3.
See www.gnu.org/licenses/gpl-3.0.html
 */
import androidx.room.*

@Entity(tableName = "Traj")
data class Trajdata(
    @PrimaryKey val type: Int,
    val desc: String?,
    val icon: Int?
)
