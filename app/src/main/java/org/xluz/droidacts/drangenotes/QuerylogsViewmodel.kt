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
    var sessShotCount: Int? = null                    // maintain by Mainactivity
    var sessTeetime: Long? = 0                        // unix time in seconds
    var shots: ArrayList<Shotdata> = arrayListOf()    // only use by Mainactivity, 2ndFrag to store current session shots
    var golfernames: ArrayList<String> = arrayListOf()
    var golfernum = arrayListOf<Int>()                // alt syntax
    var sticksnames = arrayOf<String>()
    var vRecentQuery: Int = 0
    var vGolfer: Int = 0    // =NA/any , use golferSN
    var vLogsText: MutableLiveData<String> = MutableLiveData()
    var vInfobarText = "shots info: "  // debugging use

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
            var shotsOutstr = ""
            when(vRecentQuery) {
                0 -> {
                    if(vGolfer==0) shotsOutstr = convShotsqueryToStr(theDB?.theDAO()?.getLastNShots(20))
                    else shotsOutstr = convShotsqueryToStr(theDB?.theDAO()?.getLastNShotsBy(20, vGolfer))
                }
                1 -> {
                    when(sessShotCount) {
                        null -> shotsOutstr = "No shot!"
                        0    -> shotsOutstr = "No shot"
                        else -> {
                            if(vGolfer==0)
                                shotsOutstr = convShotsqueryToStr(theDB?.theDAO()?.getLastNShots(sessShotCount ?: 20))
                            else
                                shotsOutstr = convShotsqueryToStr(theDB?.theDAO()?.getLastNShotsBySince(sessShotCount ?: 20, vGolfer, sessTeetime ?: 0))
                        }
                    }
                }
                2 -> {
                    val currT = java.util.Date().time / 1000
                    if(vGolfer==0) shotsOutstr = convShotsqueryToStr(theDB?.theDAO()?.getRecentdayShots(currT))
                    else shotsOutstr = convShotsqueryToStr(theDB?.theDAO()?.getRecentdayShotsBy(currT, vGolfer))
                }
                3 -> {
                    if(vGolfer==0) shotsOutstr = convShotsqueryToStr(theDB?.theDAO()?.getALLShots())
                    else shotsOutstr = convShotsqueryToStr(theDB?.theDAO()?.getALLShotsBy(vGolfer))
                }
            }
            vLogsText.postValue(shotsOutstr)
        }
    }

    private fun convShotsqueryToStr(shotsquery: List<Shotdata>?): String {
        // e.g. name [club] at powerlevel >> yards (shape) { comments }
        var tmpstr: String
        if(shotsquery != null) {
            tmpstr = "Query: ${shotsquery.size} \n"
            for (ss in shotsquery) {
                for(j in 0 until golfernum.size) {
                    if(ss.golfer == golfernum[j]) {    // inner join
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
        //vLogsText.postValue(tmpstr)
        return tmpstr
    }

//TODO fun convToJson(allshots): String  ??

}