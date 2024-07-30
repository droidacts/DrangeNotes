package org.xluz.droidacts.drangenotes
/* An Android app to record shots distances during driving range practice
Copyright(C) 2024 by Cecil Cheung PhD

This source code file is released under GNU General Public License version 3.
See www.gnu.org/licenses/gpl-3.0.html
 */

import android.app.Activity
import android.os.Bundle
import android.content.Context
import android.content.Intent
import android.util.Log
import java.util.Date
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.snackbar.Snackbar
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import org.xluz.droidacts.drangenotes.databinding.ActivityMainBinding

private const val SETTINGKEY1 = "appSettings_use_meters"
private const val LASTSESSIONLOG = "last_session_data"
private const val LASTSESSIONLOG_T = "tee_time"
//private const val LASTSESSIONLOG_MISC = "info"
private const val LASTSESSIONLOG_KEYALL = "all_shots"

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewmodel: OneShotdataViewmodel
    private lateinit var theDB: CCgolfDB
    private val sqliteVer: String by lazy { if(::theDB.isInitialized) theDB.theDAO().getSqliteVer() else "??" }
    private val manyShots: QuerylogsViewmodel by viewModels()    // should be scoped to this Activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)    // prevent dark mode

        theDB = CCgolfDB.get(this)    // init the DB connection
        mainViewmodel = ViewModelProvider(this).get( OneShotdataViewmodel::class.java )
        if(mainViewmodel.golfername.isEmpty()) mainViewmodel.loadGolfernames()
        if(manyShots.sticksnames.isEmpty())    // fallback, just in case
            manyShots.sticksnames = resources.getStringArray(R.array.default_sticknames)
        clrManyShots()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.fab.setOnClickListener {
            mainViewmodel.packShotdata()
            val singleshotv = mainViewmodel.singleShot.value
            if ((singleshotv != null) && mainViewmodel.datrdy) {
                singleshotv.settimeStamp()
                manyShots.shots.add(singleshotv)
                if(mainViewmodel.logCurrShot())
                    Snackbar.make(it,
                        "Shot(" + manyShots.shots.size + ") " + singleshotv.toString(), Snackbar.LENGTH_LONG)
                        .setAction("Logging", null).show()
                else
                    Snackbar.make(it, "Error logging; invalid DB?", Snackbar.LENGTH_LONG)
                        .setAction("Error!", null).show()
            } else
                Snackbar.make(it, "Nothing to log.", Snackbar.LENGTH_LONG).show()
            mainViewmodel.datrdy = false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        val appSharedpref0 = getPreferences(Context.MODE_PRIVATE)
        // some preference settings are also menu items
        menu.findItem(R.id.menu_settings).isChecked = appSharedpref0
            .getBoolean(SETTINGKEY1, false)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.menu_about -> {
/*      // Toasts allow more texts than Snackbars?
                Snackbar.make(binding.root,
                    "ver "+BuildConfig.VERSION_NAME+" by CC", Snackbar.LENGTH_LONG)
                    .setAction("More", null).show()
*/
                Toast.makeText(this,
                    "\u2022 Ver "+BuildConfig.VERSION_NAME+" Copyright 2024 by CC\n\tDistribute under GPL v3"+
                    "\n\u2022 Sys fw: API ${android.os.Build.VERSION.SDK_INT}, SQLite: $sqliteVer",
                    Toast.LENGTH_LONG).show()
                true
            }
            R.id.menu_settings -> {    // this setting is unused/unimplemented
                item.isChecked = !item.isChecked        //toggle
                val appSharedpref0 = getPreferences(Context.MODE_PRIVATE)
                with(appSharedpref0.edit()) {
                    putBoolean(SETTINGKEY1, item.isChecked)
                    apply()
                }
                true
            }
            R.id.menu_clearlogs -> {   // currently disabled, should use alertdialog to confirm?
                clrManyShots()
                true
            }
            R.id.menu_export -> {      // backup copy DB to media storage
                Snackbar.make(binding.root,
                    "Requesting a user select directory", Snackbar.LENGTH_LONG
                ).show()
                // Choose a directory using the system's file picker; only working way since sdk 33
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
                    // Optionally, specify an initial directory URI
                    //putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri)
                }
                retSelStorageDir.launch(intent)
                true
            }
            R.id.menu_logs -> {    // to logs fragment
                stashManyShots()

                val teetime = getSharedPreferences(LASTSESSIONLOG, MODE_PRIVATE).getLong(LASTSESSIONLOG_T, 0)
                val outstr = " ${manyShots.shots.size} since ${Date(teetime*1000)} "

                findNavController(R.id.nav_host_fragment_content_main).  //.navigate(R.id.logsFragment)
                    navigate(NavGraphDirections.actionGlobalLogsFragment(outstr, manyShots.shots.size, teetime))
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        /* // if fragment tag is replaced by fragmentContainerView as recommended, then
           // the fragment has to be found before finding the navController
        val hFrag=supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main)
        val navController = hFrag.navController
         */
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val sessAllshots = stashManyShots()
        outState.putString(LASTSESSIONLOG_KEYALL, sessAllshots)
    }

    private fun clrManyShots() : Long {
        manyShots.shots.clear()
        //manyShots.sessShotCount = 0
        val teetime0 = Date().time / 1000
        manyShots.sessTeetime = teetime0
        val appSharedpref1 = getSharedPreferences(LASTSESSIONLOG, MODE_PRIVATE)
        with(appSharedpref1.edit()) {
            putLong(LASTSESSIONLOG_T, teetime0)
            apply()
        }
        return teetime0
    }

    private fun stashManyShots() : String {
//        var outstr = ""
//        for (itm in manyShots.shots)
//            outstr += itm.toString() + "\n"
        // current session shots data also stored as JSON array
        val jsonOutstr = Gson().toJson(manyShots.shots)
        // mostly for debugging
        val appSharedpref1 = getSharedPreferences(LASTSESSIONLOG, MODE_PRIVATE)
        with(appSharedpref1.edit()) {
            putString(LASTSESSIONLOG_KEYALL, jsonOutstr)
            apply()
        }
        return jsonOutstr
    }

    private val retSelStorageDir = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if(it.resultCode==Activity.RESULT_OK) {
            val userFileDir = it.data?.data!!  //suppose to be a uri
            Log.d("FileOp", userFileDir.toString())

            if(CCgolfDB.copyAppDBtoSD(applicationContext, userFileDir)) {
                //Snackbar.make(binding.root,
                Toast.makeText(this,
                    CCgolfDB.DBfilename+ " to\n"+userFileDir.toString() ,
                    Toast.LENGTH_LONG
                ).show()
            } else
                Snackbar.make(binding.root,
                    "Database export copy failed!\nSwipe to dismiss", Snackbar.LENGTH_INDEFINITE
                ).show()
        }
    }

}