package org.xluz.droidacts.drangenotes

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class QuerylogsViewmodel : ViewModel() {
    var golfernames: ArrayList<String> = arrayListOf()
    var golfernum = arrayListOf<Int>()   // just diff syntax
    var sticksnames = arrayOf<String>()
    var vRecentQuery: Int = 0
    var vGolfer: Int = 0    // =All/any
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
        var tmpstr: String
        if(shotsquery != null) {
            tmpstr = "Query: ${shotsquery.size} \n"
            for (ss in shotsquery) {
                //tmpstr += ss.toString() + "\n"
                tmpstr += golfernames[ss.golfer?: 0]
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