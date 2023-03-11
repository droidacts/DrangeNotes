package org.xluz.droidacts.drangenotes

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import org.xluz.droidacts.drangenotes.databinding.FragmentFirstBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
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

        binding.buttonFirst.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
        //The folllowing codes make a button group which only 1 of 3 buttons can be checked
        binding.buttonSt.setOnClickListener{
            if(binding.buttonSt.isChecked) {
                binding.buttonTurnLeft.isChecked = false
                binding.buttonTurnRight.isChecked = false
            }
        }
        binding.buttonTurnLeft.setOnClickListener{
            if(binding.buttonTurnLeft.isChecked) {
                binding.buttonTurnRight.isChecked = false
                binding.buttonSt.isChecked = false
            }
        }
        binding.buttonTurnRight.setOnClickListener{
            if(binding.buttonTurnRight.isChecked) {
                binding.buttonSt.isChecked = false
                binding.buttonTurnLeft.isChecked = false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}