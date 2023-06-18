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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mViewmodel3.datrdy = false
        querylogsViewmodel0.sessShotCount = args.sesscount
        querylogsViewmodel0.sessTeetime = args.sessteetime
        binding.exportButton.setOnClickListener {
            val exportAllCurrShotsReq = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, binding.allshotslogs.text)
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.logs_fragment_label))
            }
            val chooser = Intent.createChooser(
                exportAllCurrShotsReq, "Export current shots data"
            )
            startActivity(chooser)
        }
        binding.queryButton.setOnClickListener {
            querylogsViewmodel0.vGolfer = binding.players3.selectedItemPosition
            querylogsViewmodel0.vRecentQuery = binding.recentchoices.selectedItemPosition
            querylogsViewmodel0.getlogs()
            binding.allshotslogs.text = querylogsViewmodel0.vLogsText
        }
        binding.allshotslogs.setOnFocusChangeListener {  //temporary use
                _, hasFocus ->
            if(hasFocus) {
                binding.allshotslogs.text = querylogsViewmodel0.vLogsText
            }
        }
        binding.allshotslogs.setOnClickListener {
            //  temporary
            binding.allshotslogs.text = querylogsViewmodel0.vLogsText
        }
// set default logs to be all shots in current session
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                querylogsViewmodel0.vGolfer = 0
                querylogsViewmodel0.vRecentQuery = 0
                querylogsViewmodel0.getlogs()
                binding.allshotslogs.text = "${args.msgtxt}\n${querylogsViewmodel0.vLogsText}"
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}