package org.xluz.droidacts.drangenotes
/**
Definition of the app database
Copyright(C) 2024 by Cecil Cheung PhD

This source code file is released under GNU General Public License version 3.
See www.gnu.org/licenses/gpl-3.0.html
 */
import android.content.Context
import android.os.Environment
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SimpleSQLiteQuery
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

@Database(
    entities = [Shotdata::class, Golferdata::class, Pleveldata::class, Stickdata::class, Trajdata::class, Bagdata::class],
    version = 3, exportSchema = false
)
abstract class CCgolfDB : RoomDatabase() {
    abstract fun theDAO(): CCgolfDBDao

    companion object {
        const val DBfilename = "drangelogsDB.db"

        // singleton
        private var oneINSTANCE: CCgolfDB? = null
        private var DBfullpath: File? = null

        fun get(context: Context): CCgolfDB {
            if (oneINSTANCE == null) {
                oneINSTANCE = Room.databaseBuilder(
                    context.applicationContext,
                    CCgolfDB::class.java,
                    DBfilename
                )
                    .createFromAsset("roomCCgolf.db")
                    .allowMainThreadQueries()    //for testing
                    .build()
                DBfullpath = context.getDatabasePath(DBfilename)
            }
            return oneINSTANCE as CCgolfDB
        }

        fun getOne(): CCgolfDB? {
            return oneINSTANCE
        }

        /*    Merge WAL back to DB
         */
        private fun compacDB(): Int {
            var retv = -1
            val c = oneINSTANCE?.query(SimpleSQLiteQuery("PRAGMA wal_checkpoint(TRUNCATE)"))
            if (c != null && c.moveToFirst())
                if (c.columnCount == 3)
                    retv = c.getInt(2)

            return retv
        }

        fun copyAppDBtoSD(): Boolean {  //may not work in SDK 33+
            val outdir = Environment.getExternalStoragePublicDirectory("backup").absolutePath
            val infile = DBfullpath
                ?: File("/data/data/org.xluz.droidacts.drangenotes/databases/" + DBfilename)
            if (infile.exists()) {
                CoroutineScope(Dispatchers.IO).launch {
                    compacDB()
                    if (!File(outdir).exists()) {
                        File(outdir).mkdir()
                    }
                    val outfile = File("$outdir/$DBfilename")
                    if (File(outdir).exists()) {
                        try {
                            withContext(Dispatchers.IO) {
                                FileInputStream(infile).channel
                            }.use { src ->
                                FileOutputStream(outfile).channel.use { dst ->
                                    dst.transferFrom(src, 0, src.size())
                                }
                            }
                        } catch (e: Exception) {    // copy fail
                            //log the error?
                        }
                    }

                }
            } else
                return false
            return true
        }
    }
}