package org.xluz.droidacts.drangenotes

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

private const val currKey = "last_shot"

class OneShotdataViewmodel(private val savedStateHandle: SavedStateHandle) : ViewModel() {

    val singleShot = MutableLiveData<Shotdata>()
    var golfername = arrayListOf<String>()
    var golfersn = arrayListOf<Int>()

    var vGolfer: Int      // selected item position in a spinner
        get() = savedStateHandle.get<Int>(currKey+"_golfer") ?: 0
        set(value) = savedStateHandle.set(currKey+"_golfer", value)
    var vStick: Int
        get() = savedStateHandle.get<Int>(currKey+"_stick") ?: 0
        set(value) = savedStateHandle.set(currKey+"_stick", value)
    var vPower: Int
        get() = savedStateHandle.get<Int>(currKey+"_power") ?: 0
        set(value) = savedStateHandle.set(currKey+"_power", value)
    var vComment: String
        get() = savedStateHandle.get<String>(currKey+"_comment") ?: ""
        set(value) = savedStateHandle.set(currKey+"_comment", value)
    var vDist: Float
        get() = savedStateHandle.get<Float>(currKey+"_dist") ?: 0.0f
        set(value) = savedStateHandle.set(currKey+"_dist", value)
    var datrdy: Boolean = false
    var updatelog = -1    // viewmdel updated by anyone(0) 1stFrag(1) 2ndFrag(2) ...

    init {
        val s = Shotdata(vDist, vPower, vStick, 0, vGolfer, "", vComment)
        singleShot.value = s.copy()
        loadGolfernames()
    }

    fun loadGolfernames(): Int {
        val theDB = CCgolfDB.getOne()
        val golfers = theDB?.theDAO()?.getGolfers()
        golfersn.clear()
        golfername.clear()
        golfers?.run {
            for (itm in this) {
                golfername.add(itm.name)
                golfersn.add(itm.id)
            }
        }
        return golfername.size
    }

    fun logCurrShot(): Boolean {
        val theDB = CCgolfDB.getOne()
        if(theDB == null) return false
        val currShot: Shotdata = singleShot.value!!
        currShot.golfer?.let { currShot.golfer = golfersn[it] }
        viewModelScope.launch {
            theDB.theDAO().logOneshot(currShot)
        }
        return true
    }

}