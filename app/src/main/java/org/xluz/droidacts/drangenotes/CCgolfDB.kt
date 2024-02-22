package org.xluz.droidacts.drangenotes
/**
Definition of the app database
Copyright(C) 2024 by Cecil Cheung PhD

This source code file is released under GNU General Public License version 3.
See www.gnu.org/licenses/gpl-3.0.html
 */

import android.content.Context
import android.util.Log
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
import java.nio.channels.Channels
import android.net.Uri
import androidx.documentfile.provider.DocumentFile

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

        fun get(actmain: Context): CCgolfDB {
            if (oneINSTANCE == null) {
                oneINSTANCE = Room.databaseBuilder(
                    actmain.applicationContext,
                    CCgolfDB::class.java,
                    DBfilename
                )
                    .createFromAsset("roomCCgolf-v3c.db")
                    .allowMainThreadQueries()    //for testing
                    .build()
                DBfullpath = actmain.getDatabasePath(DBfilename)
                Log.d("appDB", "generating new .db from asset")
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

        fun copyAppDBtoSD(appContext: Context, outdir: Uri): Boolean {
            val outfile = DocumentFile.fromTreeUri(appContext, outdir)
                ?.createFile("application/x-sqlite3", DBfilename)
            Log.d("FileOp", appContext.contentResolver.getType(outfile!!.uri) ?: "Null!")
            val outs = outfile.uri.let { uri ->
                appContext.contentResolver.openOutputStream(uri)
            }
            val infile = DBfullpath
                ?: File(appContext.filesDir.canonicalFile,"/databases/$DBfilename")    // fallback
            Log.d("FileOp", infile.toString())
            Log.d("FileOp",File(appContext.filesDir.canonicalFile,"/databases/$DBfilename").toString())
            if (infile.exists() && outs!=null) {
                CoroutineScope(Dispatchers.IO).launch {
                    compacDB()
//
                        try {
                            withContext(Dispatchers.IO) {
                                FileInputStream(infile).channel}.use { src ->
                                Channels.newChannel(outs).use { dst ->
                                    //dst.transferFrom(src, 0, src.size())
                                    src.transferTo(0, src.size(), dst)
                                }
                            }
                        } catch (e: Exception) {    // copy fail
                            Log.d("FileOp", "outstream IO error")
                        }
                }
            } else
                return false
            return true
        }
    }
}