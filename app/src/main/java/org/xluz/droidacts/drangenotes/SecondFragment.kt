package org.xluz.droidacts.drangenotes
/**
An Android app to record shots distances during driving range practice
Copyright(C) 2023 by Cecil Cheung PhD

This source code file is released under GNU General Public License version 3.
See www.gnu.org/licenses/gpl-3.0.html
 */

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.core.view.children
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import org.xluz.droidacts.drangenotes.databinding.FragmentSecondBinding

class SecondFragment : Fragment() {

    private val mViewmodel2: OneShotdataViewmodel by activityViewModels()
    private val currShots: QuerylogsViewmodel by activityViewModels()
    private var mShotdata1 = Shotdata()

    private var _binding: FragmentSecondBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mViewmodel2.datrdy = false
        mShotdata1 = mViewmodel2.singleShot.value!!

        binding.buttonSecond.setOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }

        binding.checkMiss.setOnClickListener {
            if(binding.checkMiss.isChecked) {
                binding.checkMiss.isChecked = false
                binding.checkMiss.setText(R.string.button_missed_0)
            } else {
                binding.checkMiss.isChecked = true
                binding.checkMiss.setText(R.string.button_missed_1)
            }
            saveToViewmodel()
        }
        // linking these 3 buttons into a button group
        binding.checkSt.setOnClickListener {
            binding.checkSt.isChecked = !binding.checkSt.isChecked
            if(binding.checkSt.isChecked) {
                binding.checkSt.setText(R.string.button_straight_1)
                binding.checkL.setText(R.string.button_left_0)
                binding.checkR.setText(R.string.button_right_0)
                binding.checkL.isChecked = false
                binding.checkR.isChecked = false
            } else {
                binding.checkSt.setText(R.string.button_straight_0)
            }
            saveToViewmodel()
        }
        binding.checkL.setOnClickListener {
            binding.checkL.isChecked = !binding.checkL.isChecked
            if(binding.checkL.isChecked) {
                binding.checkL.setText(R.string.button_left_1)
                binding.checkSt.setText(R.string.button_straight_0)
                binding.checkR.setText(R.string.button_right_0)
                binding.checkSt.isChecked = false
                binding.checkR.isChecked = false
            } else {
                binding.checkL.setText(R.string.button_left_0)
            }
            saveToViewmodel()
        }
        binding.checkR.setOnClickListener {
            binding.checkR.isChecked = !binding.checkR.isChecked
            if(binding.checkR.isChecked) {
                binding.checkR.setText(R.string.button_right_1)
                binding.checkSt.setText(R.string.button_straight_0)
                binding.checkL.setText(R.string.button_left_0)
                binding.checkSt.isChecked = false
                binding.checkL.isChecked = false
            } else {
                binding.checkR.setText(R.string.button_right_0)
            }
            saveToViewmodel()
        }
        //The folllowing codes make a button group which only 1 of 3 buttons can be checked
        binding.buttonSt?.setOnClickListener{
            if(binding.buttonSt?.isChecked == true) {
                binding.buttonTurnLeft?.let { it.isChecked = false }
                binding.buttonTurnRight?.let { it.isChecked = false }
            }
            saveToViewmodel()
        }
        binding.buttonTurnLeft?.setOnClickListener{
            if(binding.buttonTurnLeft?.isChecked == true) {
                binding.buttonTurnRight?.let { it.isChecked = false }
                binding.buttonSt?.let { it.isChecked = false }
            }
            saveToViewmodel()
        }
        binding.buttonTurnRight?.setOnClickListener{
            if(binding.buttonTurnRight?.isChecked == true) {
                binding.buttonSt?.let { it.isChecked = false }
                binding.buttonTurnLeft?.let { it.isChecked = false }
            }
            saveToViewmodel()
        }
        binding.buttonMiss?.setOnClickListener {
            saveToViewmodel()
        }

        val allEditbox = binding.gridLayout.children.filter { it is EditText }
        for(bx in allEditbox) {
            if(bx is EditText)
                bx.addTextChangedListener {
                    if(bx.text.toString() != "") {
                        mShotdata1.dist = it.toString().toFloat()
                    }
                    else {
                        mShotdata1.dist = -1.0f
                    }
                    saveToViewmodel()
                }
            bx.setOnFocusChangeListener { v, hasFocus ->
                //clrAllEditboxes()
                val cellsizex = (binding.URcell.right - binding.ULcell.right)/4.0     // in pixels?
                val cellsizey = (binding.LLcell.bottom - binding.ULcell.bottom)/14.0
                if(hasFocus) {
                    (v as EditText).text.clear()
                    mShotdata1.power = 5 - ((v.right - binding.ULcell.right)/cellsizex + 0.5).toInt()
                    mShotdata1.stick = ((v.bottom - binding.ULcell.bottom)/cellsizey + 0.5).toInt()
                }
            }
        }

        binding.commentboxLong.addTextChangedListener {
            mShotdata1.comment = binding.commentboxLong.text.toString()
            saveToViewmodel()
        }
        val ada = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, mViewmodel2.golfername)
        ada.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.playername2.adapter = ada
        binding.playername2.setSelection(0)
        binding.playername2.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                saveToViewmodel()
                loadtoNotesGrid()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // no need
            }
        }
    }

    private fun restoreFrViewmodel() {
// load data from viewmodel
        mShotdata1.dist = -1.0f
        when (mShotdata1.sshape) {
            0 -> {
                binding.buttonTurnLeft?.let { it.isChecked = false }
                binding.buttonTurnRight?.let { it.isChecked = false }
                binding.buttonSt?.let { it.isChecked = false }
                binding.buttonMiss?.let { it.isChecked = false }
                binding.checkL.isChecked = false
                binding.checkL.setText(R.string.button_left_0)
                binding.checkR.isChecked = false
                binding.checkR.setText(R.string.button_right_0)
                binding.checkSt.isChecked = false
                binding.checkSt.setText(R.string.button_straight_0)
                binding.checkMiss.isChecked = false
                binding.checkMiss.setText(R.string.button_missed_0)
            }
            1 -> {
                binding.buttonTurnLeft?.let { it.isChecked = false }
                binding.buttonTurnRight?.let { it.isChecked = false }
                binding.buttonSt?.let { it.isChecked = true }
                binding.buttonMiss?.let { it.isChecked = false }
                binding.checkL.isChecked = false
                binding.checkL.setText(R.string.button_left_0)
                binding.checkR.isChecked = false
                binding.checkR.setText(R.string.button_right_0)
                binding.checkSt.isChecked = true
                binding.checkSt.setText(R.string.button_straight_1)
                binding.checkMiss.isChecked = false
                binding.checkMiss.setText(R.string.button_missed_0)
            }
            2 -> {
                binding.buttonTurnLeft?.let { it.isChecked = true }
                binding.buttonTurnRight?.let { it.isChecked = false }
                binding.buttonSt?.let { it.isChecked = false }
                binding.buttonMiss?.let { it.isChecked = false }
                binding.checkL.isChecked = true
                binding.checkL.setText(R.string.button_left_1)
                binding.checkR.isChecked = false
                binding.checkR.setText(R.string.button_right_0)
                binding.checkSt.isChecked = false
                binding.checkSt.setText(R.string.button_straight_0)
                binding.checkMiss.isChecked = false
                binding.checkMiss.setText(R.string.button_missed_0)
            }
            3 -> {
                binding.buttonTurnLeft?.let { it.isChecked = false }
                binding.buttonTurnRight?.let { it.isChecked = true }
                binding.buttonSt?.let { it.isChecked = false }
                binding.buttonMiss?.let { it.isChecked = false }
                binding.checkL.isChecked = false
                binding.checkL.setText(R.string.button_left_0)
                binding.checkR.isChecked = true
                binding.checkR.setText(R.string.button_right_1)
                binding.checkSt.isChecked = false
                binding.checkSt.setText(R.string.button_straight_0)
                binding.checkMiss.isChecked = false
                binding.checkMiss.setText(R.string.button_missed_0)
            }
            4 -> {
                binding.buttonTurnLeft?.let { it.isChecked = false }
                binding.buttonTurnRight?.let { it.isChecked = false }
                binding.buttonSt?.let { it.isChecked = false }
                binding.buttonMiss?.let { it.isChecked = true }
                binding.checkL.isChecked = false
                binding.checkL.setText(R.string.button_left_0)
                binding.checkR.isChecked = false
                binding.checkR.setText(R.string.button_right_0)
                binding.checkSt.isChecked = false
                binding.checkSt.setText(R.string.button_straight_0)
                binding.checkMiss.isChecked = true
                binding.checkMiss.setText(R.string.button_missed_1)
            }
            5 -> {
                binding.buttonTurnLeft?.let { it.isChecked = false }
                binding.buttonTurnRight?.let { it.isChecked = false }
                binding.buttonSt?.let { it.isChecked = true }
                binding.buttonMiss?.let { it.isChecked = true }
                binding.checkL.isChecked = false
                binding.checkL.setText(R.string.button_left_0)
                binding.checkR.isChecked = false
                binding.checkR.setText(R.string.button_right_0)
                binding.checkSt.isChecked = true
                binding.checkSt.setText(R.string.button_straight_1)
                binding.checkMiss.isChecked = true
                binding.checkMiss.setText(R.string.button_missed_1)
            }
            6 -> {
                binding.buttonTurnLeft?.let { it.isChecked = true }
                binding.buttonTurnRight?.let { it.isChecked = false }
                binding.buttonSt?.let { it.isChecked = false }
                binding.buttonMiss?.let { it.isChecked = true }
                binding.checkL.isChecked = true
                binding.checkL.setText(R.string.button_left_1)
                binding.checkR.isChecked = false
                binding.checkR.setText(R.string.button_right_0)
                binding.checkSt.isChecked = false
                binding.checkSt.setText(R.string.button_straight_0)
                binding.checkMiss.isChecked = true
                binding.checkMiss.setText(R.string.button_missed_1)
            }
            7 -> {
                binding.buttonTurnLeft?.let { it.isChecked = false }
                binding.buttonTurnRight?.let { it.isChecked = true }
                binding.buttonSt?.let { it.isChecked = false }
                binding.buttonMiss?.let { it.isChecked = true }
                binding.checkL.isChecked = false
                binding.checkL.setText(R.string.button_left_0)
                binding.checkR.isChecked = true
                binding.checkR.setText(R.string.button_right_1)
                binding.checkSt.isChecked = false
                binding.checkSt.setText(R.string.button_straight_0)
                binding.checkMiss.isChecked = true
                binding.checkMiss.setText(R.string.button_missed_1)
            }
        }

        mShotdata1.golfer?.let { binding.playername2.setSelection(it) }
        binding.commentboxLong.setText(mShotdata1.comment)
        mShotdata1.golfer?.let { currShots.vGolfer = it }
    }

    private fun clrAllEditboxes() {
        val allEditboxes = binding.gridLayout.children.filter { it is EditText }
        for (v in allEditboxes) {
            if (v is EditText) {
                v.hint = ""
                v.text.clear()
            }
        }
    }

    private fun saveToViewmodel() {
        var ss = 0
        if(binding.checkMiss.visibility == View.VISIBLE) {
            if(binding.checkSt.isChecked) ss = 1
            else if(binding.checkL.isChecked) ss = 2
            else if(binding.checkR.isChecked) ss = 3
            if(binding.checkMiss.isChecked) ss += 4
        } else {
            if(binding.buttonSt?.isChecked == true) ss = 1
            if(binding.buttonTurnLeft?.isChecked == true) ss = 2
            if(binding.buttonTurnRight?.isChecked == true) ss = 3
            if(binding.buttonMiss?.isChecked == true) ss +=  4
        }
        mShotdata1.sshape = ss
        mShotdata1.golfer = binding.playername2.selectedItemPosition
        mViewmodel2.datrdy = mShotdata1.dist > 0.0
        mViewmodel2.updatelog = 2
        mViewmodel2.singleShot.value = mShotdata1.copy()
    }

    private fun loadtoNotesGrid() {
        clrAllEditboxes()
        if (currShots.shots.isNotEmpty()) {
            for (ss in currShots.shots) {
                if (mShotdata1.golfer == 0 || ss.golfer == mShotdata1.golfer)
                    if (ss.power == 4) {       // 100%
                        when (ss.stick) {
                            1 -> binding.cell11.hint = ss.dist.toInt().toString()
                            2 -> binding.cell12.hint = ss.dist.toInt().toString()
                            3 -> binding.cell13.hint = ss.dist.toInt().toString()
                            4 -> binding.cell14.hint = ss.dist.toInt().toString()
                            5 -> binding.cell15.hint = ss.dist.toInt().toString()
                            6 -> binding.cell16.hint = ss.dist.toInt().toString()
                            7 -> binding.cell17.hint = ss.dist.toInt().toString()
                            8 -> binding.cell18.hint = ss.dist.toInt().toString()
                            9 -> binding.cell19.hint = ss.dist.toInt().toString()
                            10 -> binding.cell1A.hint = ss.dist.toInt().toString()
                            11 -> binding.cell1B.hint = ss.dist.toInt().toString()
                            12 -> binding.cell1C.hint = ss.dist.toInt().toString()
                            13 -> binding.cell1D.hint = ss.dist.toInt().toString()
                            14 -> binding.cell1E.hint = ss.dist.toInt().toString()
                        }
                    } else if (ss.power == 3) {  // 3/4
                        when (ss.stick) {
                            1 -> binding.cell21.hint = ss.dist.toInt().toString()
                            2 -> binding.cell22.hint = ss.dist.toInt().toString()
                            3 -> binding.cell23.hint = ss.dist.toInt().toString()
                            4 -> binding.cell24.hint = ss.dist.toInt().toString()
                            5 -> binding.cell25.hint = ss.dist.toInt().toString()
                            6 -> binding.cell26.hint = ss.dist.toInt().toString()
                            7 -> binding.cell27.hint = ss.dist.toInt().toString()
                            8 -> binding.cell28.hint = ss.dist.toInt().toString()
                            9 -> binding.cell29.hint = ss.dist.toInt().toString()
                            10 -> binding.cell2A.hint = ss.dist.toInt().toString()
                            11 -> binding.cell2B.hint = ss.dist.toInt().toString()
                            12 -> binding.cell2C.hint = ss.dist.toInt().toString()
                            13 -> binding.cell2D.hint = ss.dist.toInt().toString()
                            14 -> binding.cell2E.hint = ss.dist.toInt().toString()
                        }
                    } else if (ss.power == 2) {  // 1/2
                        when (ss.stick) {
                            1 -> binding.cell31.hint = ss.dist.toInt().toString()
                            2 -> binding.cell32.hint = ss.dist.toInt().toString()
                            3 -> binding.cell33.hint = ss.dist.toInt().toString()
                            4 -> binding.cell34.hint = ss.dist.toInt().toString()
                            5 -> binding.cell35.hint = ss.dist.toInt().toString()
                            6 -> binding.cell36.hint = ss.dist.toInt().toString()
                            7 -> binding.cell37.hint = ss.dist.toInt().toString()
                            8 -> binding.cell38.hint = ss.dist.toInt().toString()
                            9 -> binding.cell39.hint = ss.dist.toInt().toString()
                        }
                    } else if (ss.power == 1) {  // chip
                        when (ss.stick) {
                            1 -> binding.cell41.hint = ss.dist.toInt().toString()
                            2 -> binding.cell42.hint = ss.dist.toInt().toString()
                            3 -> binding.cell43.hint = ss.dist.toInt().toString()
                            4 -> binding.cell44.hint = ss.dist.toInt().toString()
                        }
                    }
            }
        }
    }
    override fun onResume() {
        activity?.findViewById<View>(R.id.fab)?.visibility = View.VISIBLE
        //clrAllEditboxes()
        loadtoNotesGrid()
        restoreFrViewmodel()
        super.onResume()
    }

    override fun onPause() {
        saveToViewmodel()
        super.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}