package org.xluz.droidacts.drangenotes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class QuerylogsViewmodel : ViewModel() {
    var vRecentQuery: Int = 0
    var vGolfer: Int = 0    // =All/any
    var vLogsText: String = ""
    var sessShotCount: Int? = null
    var sessTeetime: Long? = 0    //unix time in seconds

    private val theDB = CCgolfDB.getOne()

    fun getlogs() {
        viewModelScope.launch(Dispatchers.IO) {
            when(vRecentQuery) {
                0 -> {
                    if(vGolfer==0) convShotsqueryToStr(theDB?.theDAO()?.getLastNShots(20))
                    else convShotsqueryToStr(theDB?.theDAO()?.getLastNShotsBy(20, vGolfer))
                }
                1 -> {
                    when(sessShotCount) {
                        null -> vLogsText = "Shots: none"
                        0 -> vLogsText = "Shots: none"
                        else -> {
                            if(vGolfer==0)
                                convShotsqueryToStr(theDB?.theDAO()?.getLastNShots(sessShotCount ?: 20))
                            else
                                convShotsqueryToStr(theDB?.theDAO()?.getLastNShotsBy(sessShotCount ?: 20, vGolfer))
                        }
                    }
                }
                2 -> {
                    val Tnow = java.util.Date().time / 1000
                    if(vGolfer==0) convShotsqueryToStr(theDB?.theDAO()?.getRecentdayShots(Tnow))
                    else convShotsqueryToStr(theDB?.theDAO()?.getRecentdayShotsBy(Tnow, vGolfer))
                }
                3 -> {
                    if(vGolfer==0) convShotsqueryToStr(theDB?.theDAO()?.getALLShots())
                    else convShotsqueryToStr(theDB?.theDAO()?.getALLShotsBy(vGolfer))

                }
            }

        }
    }

    private fun convShotsqueryToStr(shotsquery: List<Shotdata>?): String {
        if(shotsquery != null) {
            vLogsText = "Shots: ${shotsquery.size} / $sessShotCount\n"
            for (ss in shotsquery)
                vLogsText += ss.toString() + "\n"
        } else {
            vLogsText = "No results."
        }
        return vLogsText
    }

}