package org.xluz.droidacts.drangenotes

data class Shotdata(
    var dist: Float = 0.0f ,
    var power: Int = 0 ,
    var stick: Int = 0 ,
    var sshape: Int = 0 ,
    var golfer : Int = 0 ,
    var loc: String = "" ,
    var comment: String = ""
)
{
    var sn: Int = 0        //probably not used, should be filled in by sql
    var datetime: Int? = null      //unix time
    override fun toString(): String {
        return golfer.toString()+","+stick.toString()+","+power.toString()+","+dist.toString()+","+sshape.toString()
    }
}