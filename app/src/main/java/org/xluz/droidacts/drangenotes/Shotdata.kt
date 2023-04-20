package org.xluz.droidacts.drangenotes

import java.util.*


// need to reconcile with table Shots in cc-golf.db
data class Shotdata(
    var dist: Float = 0.0f ,       //col 2
    var power: Int = 0 ,           //col 3
    var stick: Int = 0 ,           //col 4
    var sshape: Int = 0 ,          //col 5
    var golfer : Int = 0 ,         //col 6
    var loc: String = "" ,         //col 8
    var comment: String = ""       //col 9
)
{
    var sn: Int = 0                //col 1, probably not used, should be filled in by sqlite
    var datetime: Int? = null      //col 7, in unix time
    fun settimeStamp(): String {
        datetime = ((Date().time)/1000).toInt()
        return Date(datetime!! *1000L).toString()
    }
    override fun toString(): String {
        // ready to be used in an INSERT SQl?
        return datetime.toString()+","+
                dist.toString()+","+power.toString()+","+stick.toString()+","+sshape.toString()+","+golfer.toString()+
                ","+comment+"."
    }
}