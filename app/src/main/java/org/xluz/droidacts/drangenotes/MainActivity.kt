package org.xluz.droidacts.drangenotes
/* Description comment block
copyright, license, etc
 */
import android.app.Activity
import android.os.Bundle
import android.content.Context
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
import java.util.Date
import org.xluz.droidacts.drangenotes.databinding.ActivityMainBinding

private const val SETTINGKEY1 = "appSettings_use_meters"
private const val LASTSESSIONLOG = "last_session_data"
private const val LASTSESSIONLOG_T = "tee_time"
private const val LASTSESSIONLOG_KEY1 = "last_shot"
private const val LASTSESSIONLOG_KEYALL = "all_shots"

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private var manyShots = mutableListOf<Shotdata>()
    private lateinit var mainViewmodel: OneShotdataViewmodel
    lateinit var theDB: CCgolfDB
//    lateinit var theDAO: CCgolfDBDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        theDB = CCgolfDB.get(this)    // init the DB connection
        mainViewmodel = ViewModelProvider(this).get( OneShotdataViewmodel::class.java )

        manyShots.clear()   //making sure manyShots is accessed at least once and initialized

        binding.fab.setOnClickListener {
            val singleshotv = mainViewmodel.singleShot.value
            if((singleshotv != null) && mainViewmodel.datrdy) {
                singleshotv.settimeStamp()
                manyShots.add(singleshotv)
                if(mainViewmodel.logCurrShot())
                Snackbar.make(it, "Shot("+manyShots.size+") "+singleshotv.toString(), Snackbar.LENGTH_LONG)
                    .setAction("Logging", null).show()
                else
                    Snackbar.make(it, "Not logged; invalidDB?", Snackbar.LENGTH_LONG)
                        .setAction("Error!", null).show()
            } else
                Snackbar.make(it, "No data.", Snackbar.LENGTH_LONG).show()
        }

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
                Snackbar.make(binding.root, "ver "+BuildConfig.VERSION_NAME+" by CC", Snackbar.LENGTH_INDEFINITE)
                    .setAction("More", null).show()
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
                true
            }
            R.id.menu_export -> {
                //change this to backup/copy DB
                CoroutineScope(Dispatchers.IO).launch {
                    val c = theDB.query(SimpleSQLiteQuery("PRAGMA wal_checkpoint(TRUNCATE)"))
                    if (c.moveToFirst())
                        if (c.getInt(0) != 0) {
                            val errmsg =
                                "WAL checkpoint: (" + c.columnCount + ") " + c.getInt(0) + c.getInt(2)
                            val appSharedpref1 = getSharedPreferences(LASTSESSIONLOG, MODE_PRIVATE)
                            with(appSharedpref1.edit()) {
                                putString(LASTSESSIONLOG_KEY1, errmsg)
                                apply()
                            }
                        }
                    // code to copy file to external SD
                    DeveloperStuff.copyAppDataToLocal(parent, getString(R.string.app_name))
                }
                Snackbar.make(binding.root, "Copying app DB to external SD", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
                true
            }
            R.id.menu_logs -> {
                stashManyShots()
                var outstr = "Shots: ${manyShots.size} Curr/ "
                outstr += theDB.theDAO().getNShots().toString() + " All\n"

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
//        outState.putString(LASTSESSIONLOG_KEYALL, manyShots.toString())
        stashManyShots()
    }

    override fun onStart() {
        super.onStart()
        val appSharedpref1 = getSharedPreferences(LASTSESSIONLOG, MODE_PRIVATE)
        with(appSharedpref1.edit()) {
            putLong(LASTSESSIONLOG_T, Date().time)
            apply()
        }
    }

    private fun stashManyShots() {
        var outstr = ""
        for (itm in manyShots)
            outstr += itm.toString() + "\n"
        // mostly for debugging
        val appSharedpref1 = getSharedPreferences(LASTSESSIONLOG, MODE_PRIVATE)
        with(appSharedpref1.edit()) {
            putString(LASTSESSIONLOG_KEYALL, outstr)
            apply()
        }
    }
}