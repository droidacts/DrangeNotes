package org.xluz.droidacts.drangenotes

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import org.xluz.droidacts.drangenotes.databinding.FragmentFirstBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null
    private var mShotdata1 = Shotdata()
    val mViewmodel: OneShotdataViewmodel by activityViewModels()

    // This property is only valid between onCreateView and onDestroyView
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mShotdata1 = mViewmodel.singleShot.value!!
        binding.buttonFirst.setOnClickListener {
            saveStateToViewmodel()
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
        //The folllowing codes make a button group which only 1 of 3 buttons can be checked
        binding.buttonSt.setOnClickListener{
            if(binding.buttonSt.isChecked) {
                binding.buttonTurnLeft.isChecked = false
                binding.buttonTurnRight.isChecked = false
            }
            saveStateToViewmodel()
        }
        binding.buttonTurnLeft.setOnClickListener{
            if(binding.buttonTurnLeft.isChecked) {
                binding.buttonTurnRight.isChecked = false
                binding.buttonSt.isChecked = false
            }
            saveStateToViewmodel()
        }
        binding.buttonTurnRight.setOnClickListener{
            if(binding.buttonTurnRight.isChecked) {
                binding.buttonSt.isChecked = false
                binding.buttonTurnLeft.isChecked = false
            }
            saveStateToViewmodel()
        }
        binding.buttonMiss.setOnClickListener {
            saveStateToViewmodel()
        }

        // retrive last known selections
        binding.playernames.setSelection(mShotdata1.golfer)
        binding.sticklist.setSelection(mShotdata1.stick)
        binding.radioGroup.check(mShotdata1.power)
        binding.commentbox.setText(mShotdata1.comment)
        if(mShotdata1.dist.toInt() > 0)
            binding.DistanceYards.setText(mShotdata1.dist.toInt().toString())
        else
            binding.DistanceYards.setText("")
        when(mShotdata1.sshape) {
            0 -> {
                binding.buttonTurnLeft.isChecked = false
                binding.buttonTurnRight.isChecked = false
                binding.buttonSt.isChecked = false
                binding.buttonMiss.isChecked = false
            }
            1 -> {
                binding.buttonTurnLeft.isChecked = false
                binding.buttonTurnRight.isChecked = false
                binding.buttonSt.isChecked = true
                binding.buttonMiss.isChecked = false
            }
            2 -> {
                binding.buttonTurnLeft.isChecked = true
                binding.buttonTurnRight.isChecked = false
                binding.buttonSt.isChecked = false
                binding.buttonMiss.isChecked = false
            }
            3 -> {
                binding.buttonTurnLeft.isChecked = false
                binding.buttonTurnRight.isChecked = true
                binding.buttonSt.isChecked = false
                binding.buttonMiss.isChecked = false
            }
            4 -> {
                binding.buttonTurnLeft.isChecked = false
                binding.buttonTurnRight.isChecked = false
                binding.buttonSt.isChecked = false
                binding.buttonMiss.isChecked = true
            }
            5 -> {
                binding.buttonTurnLeft.isChecked = false
                binding.buttonTurnRight.isChecked = false
                binding.buttonSt.isChecked = true
                binding.buttonMiss.isChecked = true
            }
            6 -> {
                binding.buttonTurnLeft.isChecked = true
                binding.buttonTurnRight.isChecked = false
                binding.buttonSt.isChecked = false
                binding.buttonMiss.isChecked = true
            }
            7 -> {
                binding.buttonTurnLeft.isChecked = false
                binding.buttonTurnRight.isChecked = true
                binding.buttonSt.isChecked = false
                binding.buttonMiss.isChecked = true
            }
        }

    }

    override fun onDestroyView() {
        saveStateToViewmodel()
        super.onDestroyView()
        _binding = null
    }

    override fun onPause() {    //not sure this is needed
        super.onPause()
        saveStateToViewmodel()
    }

    private fun saveStateToViewmodel() {

        mShotdata1.golfer = binding.playernames.selectedItemPosition
        mShotdata1.stick = binding.sticklist.selectedItemPosition
        mShotdata1.power = binding.radioGroup.checkedRadioButtonId    //need map from id to text
        mShotdata1.comment = binding.commentbox.text.toString()
        mShotdata1.dist = if(binding.DistanceYards.text.toString() != "") {
                                binding.DistanceYards.text.toString().toFloat()
                            } else -1.0f
        mShotdata1.sshape = 0
        if(binding.buttonSt.isChecked) mShotdata1.sshape = 1
        if(binding.buttonTurnLeft.isChecked) mShotdata1.sshape = 2
        if(binding.buttonTurnRight.isChecked) mShotdata1.sshape = 3
        if(binding.buttonMiss.isChecked) mShotdata1.sshape += 4

        mViewmodel.singleShot.value = mShotdata1.copy()
    }
}