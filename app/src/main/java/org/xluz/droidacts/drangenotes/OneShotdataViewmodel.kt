package org.xluz.droidacts.drangenotes

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel


private const val currKey = "last_shot"

class OneShotdataViewmodel(private val savedStateHandle: SavedStateHandle) : ViewModel() {

    val singleShot = MutableLiveData<Shotdata>()

    var vGolfer: Int
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

    init {
        val s = Shotdata(vDist, vPower, vStick, 0, vGolfer, "", vComment)
        singleShot.value = s.copy()
    }


}