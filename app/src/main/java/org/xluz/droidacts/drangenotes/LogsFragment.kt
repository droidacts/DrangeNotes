package org.xluz.droidacts.drangenotes

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import kotlinx.coroutines.launch


class LogsFragment : Fragment() {

    val mViewmodel3: OneShotdataViewmodel by activityViewModels()
    private val args: LogsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val froot = inflater.inflate(R.layout.fragment_logs, container, false)
        //val logsTextbox = froot.findViewById<TextView>(R.id.allshotslogs)
        //logsTextbox.text = args.msgtxt

        return froot
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mViewmodel3.datrdy = false
        val logsTextbox = view.findViewById<TextView>(R.id.allshotslogs)
        view.findViewById<Button>(R.id.clearButton).setOnClickListener {
            //ask mainAct to clear the data
            //exit & restart the app will clear the data
            logsTextbox.text = getString(R.string.large_text)
        }
        view.findViewById<Button>(R.id.exportButton).setOnClickListener {
            val exportAllCurrShotsReq = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, logsTextbox.text)
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.logs_fragment_label))
            }
            val chooser = Intent.createChooser(
                exportAllCurrShotsReq, "Export current shots data"
            )
            startActivity(chooser)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                val theDB=CCgolfDB.getOne()
                val recentShots: List<Shotdata>? = theDB?.theDAO()?.getLast20Shots()
                var outstr = args.msgtxt + "\n"
                if (recentShots != null) {
                    for(ss in recentShots)
                        outstr += ss.toString() + "\n"
                    logsTextbox.text = outstr
                }
            }
        }
    }
}