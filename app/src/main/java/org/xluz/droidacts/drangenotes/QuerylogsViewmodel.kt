package org.xluz.droidacts.drangenotes
/*
Data class to hold values of the UI elements of various fragments/activity
Copyright(C) 2024 by Cecil Cheung PhD

This source code file is released under GNU General Public License version 3.
See www.gnu.org/licenses/gpl-3.0.html
 */

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class QuerylogsViewmodel : ViewModel() {
    var shots: ArrayList<Shotdata> = arrayListOf()
    var golfernames: ArrayList<String> = arrayListOf()
    var golfernum = arrayListOf<Int>()                // alt syntax
    var sticksnames = arrayOf<String>()
    var vRecentQuery: Int = 0
    var vGolfer: Int = 0    // =NA/any , use golferSN
    var vLogsText: MutableLiveData<String> = MutableLiveData()
    var sessShotCount: Int? = null
    var sessTeetime: Long? = 0    //unix time in seconds
    var vInfobarText = "shots info: "  // debugging

    private val theDB = CCgolfDB.getOne()

    init {
        val golfers = theDB?.theDAO()?.getGolfers()
        golfernum.clear()
        golfernames.clear()
        if (golfers != null) {
            for (pn in golfers) {
                golfernames.add(pn.name)
                golfernum.add(pn.id)
            }
        }
        theDB?.theDAO()?.getSticksDefault()?.let {
        // only the first 14 sticks are retrived, for now
            if(it.isNotEmpty()) sticksnames = it.toTypedArray()
        }
     }
    fun getlogs() {
        viewModelScope.launch(Dispatchers.IO) {
            when(vRecentQuery) {
                0 -> {
                    if(vGolfer==0) convShotsqueryToStr(theDB?.theDAO()?.getLastNShots(20))
                    else convShotsqueryToStr(theDB?.theDAO()?.getLastNShotsBy(20, vGolfer))
                }
                1 -> {
                    when(sessShotCount) {
                        null -> vLogsText.postValue("No shot")
                        0    -> vLogsText.postValue("No shot")
                        else -> {
                            if(vGolfer==0)
                                convShotsqueryToStr(theDB?.theDAO()?.getLastNShots(sessShotCount ?: 20))
                            else
                                convShotsqueryToStr(theDB?.theDAO()?.getLastNShotsBySince(sessShotCount ?: 20, vGolfer, sessTeetime ?: 0))
                        }
                    }
                }
                2 -> {
                    val currT = java.util.Date().time / 1000
                    if(vGolfer==0) convShotsqueryToStr(theDB?.theDAO()?.getRecentdayShots(currT))
                    else convShotsqueryToStr(theDB?.theDAO()?.getRecentdayShotsBy(currT, vGolfer))
                }
                3 -> {
                    if(vGolfer==0) convShotsqueryToStr(theDB?.theDAO()?.getALLShots())
                    else convShotsqueryToStr(theDB?.theDAO()?.getALLShotsBy(vGolfer))

                }
            }

        }
    }

    private fun convShotsqueryToStr(shotsquery: List<Shotdata>?): String {
        var tmpstr: String
        if(shotsquery != null) {
            tmpstr = "Query: ${shotsquery.size} \n"
            for (ss in shotsquery) {
                for(j in 0 until golfernum.size) {
                    if(ss.golfer == golfernum[j]) {
                        tmpstr += golfernames[j]
                        break
                    }
                }
                tmpstr += " [" + sticksnames[ss.stick-1] + "] "
                tmpstr += "at ${ss.power} >> ${ss.dist} (${ss.sshape}) { ${ss.comment} }\n"
            }
        } else {
            tmpstr = "No result"
        }
        vLogsText.postValue(tmpstr)
        return tmpstr
    }

}