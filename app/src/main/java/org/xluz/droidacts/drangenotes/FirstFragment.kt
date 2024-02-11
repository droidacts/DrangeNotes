package org.xluz.droidacts.drangenotes
/**
An Android app to record shots distances during driving range practice
Copyright(C) 2024 by Cecil Cheung PhD

This source code file is released under GNU General Public License version 3.
See www.gnu.org/licenses/gpl-3.0.html
 */

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import org.xluz.droidacts.drangenotes.databinding.FragmentFirstBinding

class FirstFragment : Fragment() , AdapterView.OnItemSelectedListener {

    private var _binding: FragmentFirstBinding? = null
    private lateinit var mShotdata1: Shotdata
    private val mViewmodel: OneShotdataViewmodel by activityViewModels()

    // This property is only valid between onCreateView and onDestroyView
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mViewmodel.singleShot.observe(viewLifecycleOwner, Observer {

            Log.d("ViewM", "livedata obs: "+mViewmodel.updatelog)
            if(mViewmodel.updatelog > 1) {
                mShotdata1 = it
                //restoreFrViewmodel()
                //mViewmodel.updatelog = -1
            }
        })

        val ada = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            mViewmodel.golfername)
        ada.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.playernames.adapter = ada
        binding.playernames.onItemSelectedListener = this
        binding.sticklist.onItemSelectedListener = this
        binding.commentbox.addTextChangedListener {
            mViewmodel.vComment = binding.commentbox.text.toString()
        }
        binding.DistanceYards.addTextChangedListener {
            mViewmodel.vDist = if(binding.DistanceYards.text.toString() != "") {
                binding.DistanceYards.text.toString().toFloat()
            } else -1.0f
        }
        binding.DistanceYards.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus) {
                if(v is android.widget.EditText) {
                    v.text.clear()
                }
            }
        }
/*  somehow this block is not needed ?!
        binding.DistanceYards.setOnEditorActionListener { v, actionId, event ->
            if(actionId==EditorInfo.IME_ACTION_DONE) {
                // just close the keypad
                val im = v.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                im.hideSoftInputFromWindow(v.windowToken, 0)
                true
            } else false
        }
*/
        binding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            when(binding.radioGroup.checkedRadioButtonId) {
                R.id.radioButton4 -> mViewmodel.vPower = 4
                R.id.radioButton3 -> mViewmodel.vPower = 3
                R.id.radioButton2 -> mViewmodel.vPower = 2
                R.id.radioButton1 -> mViewmodel.vPower = 1
            }
        }
        //The folllowing codes make a button group which only 1 of 3 buttons can be checked
        binding.buttonSt.setOnClickListener{
            if(binding.buttonSt.isChecked) {
                binding.buttonTurnLeft.isChecked = false
                binding.buttonTurnRight.isChecked = false
            }
            mViewmodel.vSShape = packSShape()
        }
        binding.buttonTurnLeft.setOnClickListener{
            if(binding.buttonTurnLeft.isChecked) {
                binding.buttonTurnRight.isChecked = false
                binding.buttonSt.isChecked = false
            }
            mViewmodel.vSShape = packSShape()
        }
        binding.buttonTurnRight.setOnClickListener{
            if(binding.buttonTurnRight.isChecked) {
                binding.buttonSt.isChecked = false
                binding.buttonTurnLeft.isChecked = false
            }
            mViewmodel.vSShape = packSShape()
        }
        binding.buttonMiss.setOnClickListener {
            mViewmodel.vSShape = packSShape()
        }

    }

    private fun packSShape(): Int {
        if (binding.buttonMiss.isChecked) {
            if (binding.buttonSt.isChecked)
                return 7
            else if (binding.buttonTurnLeft.isChecked)
                return 5
            else if (binding.buttonTurnRight.isChecked)
                return 6
            else
                return 4
        } else {
            if (binding.buttonSt.isChecked)
                return 3
            else if (binding.buttonTurnLeft.isChecked)
                return 1
            else if (binding.buttonTurnRight.isChecked)
                return 2
            else
                return 0
        }
    }

    private fun saveStateToViewmodel() {
        // ensure UI views states are saved in viewModels
        mViewmodel.vGolfer = binding.playernames.selectedItemPosition
        mViewmodel.vStick = binding.sticklist.selectedItemPosition + 1    //0 invalid in table; temporary
        mViewmodel.vComment = binding.commentbox.text.toString()
        mViewmodel.vDist = if(binding.DistanceYards.text.toString() != "") {
            binding.DistanceYards.text.toString().toFloat()
        } else -1.0f

        if(binding.buttonSt.isChecked) mViewmodel.vSShape = 3
        else if(binding.buttonTurnLeft.isChecked) mViewmodel.vSShape = 1
        else if(binding.buttonTurnRight.isChecked) mViewmodel.vSShape = 2
        else mViewmodel.vSShape = 0
        if(binding.buttonMiss.isChecked) mViewmodel.vSShape += 4
        when(binding.radioGroup.checkedRadioButtonId) {
            R.id.radioButton4 -> mViewmodel.vPower = 4
            R.id.radioButton3 -> mViewmodel.vPower = 3
            R.id.radioButton2 -> mViewmodel.vPower = 2
            R.id.radioButton1 -> mViewmodel.vPower = 1
        }

        mViewmodel.updatelog = 1
        mViewmodel.packShotdata()
        Log.d("ViewM", "Update singleShot in Frag1.")
    }

    private fun restoreFrViewmodel() {
        // retrive last known selections
        // note the valid values of each field
        binding.commentbox.setText(mViewmodel.vComment)
        mViewmodel.vGolfer.let { binding.playernames.setSelection(it) } // 0, 1, ..
        binding.sticklist.setSelection(mViewmodel.vStick - 1)    // 0..13; temp fix
        //binding.radioGroup.clearCheck()
        when(mViewmodel.vPower) {
            1 -> binding.radioGroup.check(R.id.radioButton1)
            2 -> binding.radioGroup.check(R.id.radioButton2)
            3 -> binding.radioGroup.check(R.id.radioButton3)
            4 -> binding.radioGroup.check(R.id.radioButton4)
        }
        if(mViewmodel.vDist.toInt() > 0)
            binding.DistanceYards.setText(mViewmodel.vDist.toInt().toString())
        else
            binding.DistanceYards.setText("")
        when(mViewmodel.vSShape) {
            0 -> {
                binding.buttonTurnLeft.isChecked = false
                binding.buttonTurnRight.isChecked = false
                binding.buttonSt.isChecked = false
                binding.buttonMiss.isChecked = false
            }
            3 -> {
                binding.buttonTurnLeft.isChecked = false
                binding.buttonTurnRight.isChecked = false
                binding.buttonSt.isChecked = true
                binding.buttonMiss.isChecked = false
            }
            1 -> {
                binding.buttonTurnLeft.isChecked = true
                binding.buttonTurnRight.isChecked = false
                binding.buttonSt.isChecked = false
                binding.buttonMiss.isChecked = false
            }
            2 -> {
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
            7 -> {
                binding.buttonTurnLeft.isChecked = false
                binding.buttonTurnRight.isChecked = false
                binding.buttonSt.isChecked = true
                binding.buttonMiss.isChecked = true
            }
            5 -> {
                binding.buttonTurnLeft.isChecked = true
                binding.buttonTurnRight.isChecked = false
                binding.buttonSt.isChecked = false
                binding.buttonMiss.isChecked = true
            }
            6 -> {
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

    override fun onResume() {
        restoreFrViewmodel()
        activity?.findViewById<View>(R.id.fab)?.visibility = View.VISIBLE
        super.onResume()
    }

    override fun onPause() {
        saveStateToViewmodel()
        super.onPause()
    }

    // Following 2 are implementing AdapterView.OnItemSelectedListener
    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("No need")
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        mViewmodel.vGolfer = binding.playernames.selectedItemPosition
        mViewmodel.vStick = binding.sticklist.selectedItemPosition + 1    //0 invalid in table; temporary fix
    }
}
