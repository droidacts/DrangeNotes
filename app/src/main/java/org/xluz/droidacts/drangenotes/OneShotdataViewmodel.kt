package org.xluz.droidacts.drangenotes
/* A viewmodel to hold the states of the UI conbtrolled by FirstFragment,
   also provide access to the app database.

Copyright(C) 2024 by Cecil Cheung PhD

This source code file is released under GNU General Public License version 3.
See www.gnu.org/licenses/gpl-3.0.html
 */

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
        get() = savedStateHandle.get<Int>(currKey + "_golfer") ?: 0
        set(value) = savedStateHandle.set(currKey + "_golfer", value)
    var vStick: Int
        get() = savedStateHandle.get<Int>(currKey + "_stick") ?: 1
        set(value) = savedStateHandle.set(currKey + "_stick", value)
    var vPower: Int
        get() = savedStateHandle.get<Int>(currKey + "_power") ?: 0
        set(value) = savedStateHandle.set(currKey + "_power", value)
    var vComment: String
        get() = savedStateHandle.get<String>(currKey + "_comment") ?: ""
        set(value) = savedStateHandle.set(currKey + "_comment", value)
    var vDist: Float
        get() = savedStateHandle.get<Float>(currKey + "_dist") ?: 0.0f
        set(value) = savedStateHandle.set(currKey + "_dist", value)
    var vSShape: Int
        get() = savedStateHandle.get<Int>(currKey + "_sshape") ?: 0
        set(value) = savedStateHandle.set(currKey + "_sshape", value)
    var datrdy: Boolean = false
    var updatelog = -1    // viewmdel updated by anyone(0) 1stFrag(1) 2ndFrag(2) ...

    init {
        val s = Shotdata(vDist, vPower, vStick, vSShape, vGolfer, "", vComment)
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
        val theDB = CCgolfDB.getOne() ?: return false
        val currShot: Shotdata = singleShot.value!!
        // logged shots store golfer SN instead of UI spinner item position
        currShot.golfer?.let { currShot.golfer = golfersn[it] }
        viewModelScope.launch {
            theDB.theDAO().logOneshot(currShot)
        }
        return true
    }

    fun packShotdata(): Boolean {
        // Note that dist and stick are NOTNULL in DB
        datrdy = (vDist > 0.0) && (vStick >= 0)
        singleShot.value = Shotdata(vDist, vPower, vStick, vSShape, vGolfer, "", vComment)
        return datrdy
    }
}