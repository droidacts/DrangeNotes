package org.xluz.droidacts.drangenotes

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import org.xluz.droidacts.drangenotes.databinding.FragmentFirstBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() , AdapterView.OnItemSelectedListener {

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

        binding.playernames.onItemSelectedListener = this
        binding.sticklist.onItemSelectedListener = this
        binding.radioGroup.setOnCheckedChangeListener { group, checkedId -> saveStateToViewmodel() }
        binding.commentbox.addTextChangedListener { saveStateToViewmodel() }
        binding.DistanceYards.addTextChangedListener { saveStateToViewmodel() }
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
        // note the valid values of each field
        binding.commentbox.setText(mShotdata1.comment)
        binding.playernames.setSelection(mShotdata1.golfer) // 0, 1, ..
        binding.sticklist.setSelection(mShotdata1.stick)    // 0..13
        binding.radioGroup.clearCheck()
        when(mShotdata1.power) {
            1 -> binding.radioGroup.check(R.id.radioButton1)
            2 -> binding.radioGroup.check(R.id.radioButton2)
            3 -> binding.radioGroup.check(R.id.radioButton3)
            4 -> binding.radioGroup.check(R.id.radioButton4)
        }
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
        mShotdata1.comment = binding.commentbox.text.toString()
        mShotdata1.dist = if(binding.DistanceYards.text.toString() != "") {
                                binding.DistanceYards.text.toString().toFloat()
                            } else -1.0f
        mShotdata1.sshape = 0
        if(binding.buttonSt.isChecked) mShotdata1.sshape = 1
        if(binding.buttonTurnLeft.isChecked) mShotdata1.sshape = 2
        if(binding.buttonTurnRight.isChecked) mShotdata1.sshape = 3
        if(binding.buttonMiss.isChecked) mShotdata1.sshape += 4
        when(binding.radioGroup.checkedRadioButtonId) {
            R.id.radioButton4 -> mShotdata1.power = 4
            R.id.radioButton3 -> mShotdata1.power = 3
            R.id.radioButton2 -> mShotdata1.power = 2
            R.id.radioButton1 -> mShotdata1.power = 1
        }

        mViewmodel.singleShot.value = mShotdata1.copy()
        mViewmodel.datrdy = true
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        saveStateToViewmodel()
    }
}