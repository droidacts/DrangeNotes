package org.xluz.droidacts.drangenotes

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import kotlinx.coroutines.launch
import org.xluz.droidacts.drangenotes.databinding.FragmentLogsBinding

class LogsFragment : Fragment() {

    private val mViewmodel3: OneShotdataViewmodel by activityViewModels()
    private val querylogsViewmodel0: QuerylogsViewmodel by viewModels()
    private val args: LogsFragmentArgs by navArgs()
    private var _binding: FragmentLogsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLogsBinding.inflate(inflater, container, false)
        // or, as noted in other sources of databinding ...
        //_binding = DataBindingUtil.inflate(inflater, R.layout.fragment_logs, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mViewmodel3.datrdy = false
        querylogsViewmodel0.sessShotCount = args.sesscount
        querylogsViewmodel0.sessTeetime = args.sessteetime
        querylogsViewmodel0.vInfobarText = args.msgtxt.toString()
        binding.exportButton.setOnClickListener {
            val exportAllCurrShotsReq = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, binding.allshotslogs.text)
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.logs_fragment_label)+binding.infobar.text)
            }
            val chooser = Intent.createChooser(
                exportAllCurrShotsReq, "Export current shots logs"
            )
            startActivity(chooser)
        }
        binding.queryButton.setOnClickListener {
            querylogsViewmodel0.vGolfer = binding.players3.selectedItemPosition
            querylogsViewmodel0.vRecentQuery = binding.recentchoices.selectedItemPosition
            querylogsViewmodel0.getlogs()
        }
// try data binding to textview, lifecycle owner is needed by LiveData
        binding.localviewm = querylogsViewmodel0
        binding.lifecycleOwner = viewLifecycleOwner
        // not sure if we need this
//        querylogsViewmodel0.vLogsText.observe(viewLifecycleOwner) {
//            binding.allshotslogs.text = it
//        }

// set default logs to be all shots in current session
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                querylogsViewmodel0.vGolfer = 0
                querylogsViewmodel0.vRecentQuery = 0
                querylogsViewmodel0.getlogs()
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}