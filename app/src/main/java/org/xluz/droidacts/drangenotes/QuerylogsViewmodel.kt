package org.xluz.droidacts.drangenotes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class QuerylogsViewmodel : ViewModel() {
    var queryOUTstr: String = ""
    var vGolfer: Int = 0    // All/any
    val theDB = CCgolfDB.getOne()

    fun getlogs() {
        viewModelScope.launch {
            theDB?.theDAO()?.getLast20Shots()
        }
    }
}