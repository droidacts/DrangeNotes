package org.xluz.droidacts.drangenotes

import android.os.Bundle
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.snackbar.Snackbar
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.ViewModelProvider
import androidx.sqlite.db.SimpleSQLiteQuery
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.xluz.droidacts.drangenotes.databinding.ActivityMainBinding
import java.util.Date

private const val SETTINGKEY1 = "appSettings_use_meters"
private const val LASTSESSIONLOG = "last_session_data"
private const val LASTSESSIONLOG_KEY1 = "last_shot"
private const val LASTSESSIONLOG_KEYALL = "all_shots"

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private var manyShots = mutableListOf<Shotdata>()
    private lateinit var mainViewmodel: OneShotdataViewmodel
    lateinit var theDB: CCgolfDB
    lateinit var theDAO: CCgolfDBDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        manyShots.clear()   //making sure manyShots is accessed at least once and initialized

        binding.fab.setOnClickListener {

            mainViewmodel = ViewModelProvider(this).get( OneShotdataViewmodel::class.java )
            val singleshotv = mainViewmodel.singleShot.value
            if((singleshotv != null) && mainViewmodel.datrdy) {
                singleshotv.settimeStamp()
                manyShots.add(singleshotv)
                val appSharedpref1 = getSharedPreferences(LASTSESSIONLOG, Context.MODE_PRIVATE)
                with(appSharedpref1.edit()) {
                    putString(LASTSESSIONLOG_KEY1, singleshotv.toString())
                    apply()
                }
                mainViewmodel.logCurrShot()
//                CoroutineScope(Dispatchers.IO).launch {
//                    theDAO.logOneshot(singleshotv)
//                }
                Snackbar.make(it, "Shot("+manyShots.size+") "+singleshotv.toString(), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            } else
                Snackbar.make(it, "No data.", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        theDB = CCgolfDB.get(this)
        theDAO = theDB.theDAO()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        val appSharedpref0 = getPreferences(Context.MODE_PRIVATE)
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
                Snackbar.make(binding.root, "ver "+BuildConfig.VERSION_NAME+" by CC", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
                true
            }
            R.id.menu_settings -> {
                item.isChecked = !item.isChecked        //toggle
                val appSharedpref0 = getPreferences(Context.MODE_PRIVATE)
                with(appSharedpref0.edit()) {
                    putBoolean(SETTINGKEY1, item.isChecked)
                    apply()
                }
                true
            }
            R.id.menu_clearlogs -> {   // currently disabled, should use alertdialog to confirm?
                manyShots.clear()
                val c = theDB.query(SimpleSQLiteQuery("PRAGMA wal_checkpoint(TRUNCATE)"))
                if(c.moveToFirst())
                Snackbar.make(binding.root, "WAL checkpoint: (${c.columnCount}) ${c.getInt(0)}",
                    Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
                true
            }
            R.id.menu_export -> {   // currently disabled awaiting refactoring
                //change this to backup/copy DB
                val exportAllCurrShotsReq = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, manyShots.toString())
                }
                val chooser = Intent.createChooser(
                    exportAllCurrShotsReq, "Export current shots data"
                )
                startActivity(chooser)
                true
            }
            R.id.menu_logs -> {
                //val timenow = (Date().time/1000L).toInt()
                //manyShots = theDAO.getRecentShots(timenow).toMutableList()
                stashManyShots()
                var outstr = "Shots: "
                outstr += manyShots.size.toString() + " Curr/ " + theDAO.getNShots().toString() + " All\n"
                //for (itm in manyShots)
                //    outstr += itm.toString() + "\n"
                findNavController(R.id.nav_host_fragment_content_main).  //.navigate(R.id.logsFragment)
                    navigate(NavGraphDirections.actionGlobalLogsFragment(outstr))
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
        outState.putString(LASTSESSIONLOG_KEYALL, manyShots.toString())
        stashManyShots()
    }

    override fun onStart() {
        super.onStart()
        val appSharedpref1 = getSharedPreferences(LASTSESSIONLOG, MODE_PRIVATE)
        with(appSharedpref1.edit()) {
            putLong("tee_time", Date().time)
            apply()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        val c = theDB.query(SimpleSQLiteQuery("PRAGMA wal_checkpoint(TRUNCATE)"))
        if(c.moveToFirst())
            c.getInt(0)
    }
    private fun stashManyShots() {
        var outstr = ""
        for (itm in manyShots)
            outstr += itm.toString() + "\n"
        // use to pass data among fragments
        val appSharedpref1 = getSharedPreferences(LASTSESSIONLOG, MODE_PRIVATE)
        with(appSharedpref1.edit()) {
            putString(LASTSESSIONLOG_KEYALL, outstr)
            apply()
        }
    }
}