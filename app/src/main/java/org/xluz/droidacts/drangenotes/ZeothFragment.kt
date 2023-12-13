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
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2

/**
 * sample [Fragment] subclass.
 * also see developer.android.com/guide/navigation/navigation-swipe-view-2
 */
class ZeothFragment : Fragment() {

    private lateinit var viewpager: ViewPager2
    private lateinit var uipages: UIpages

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_zeoth, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewpager = view.findViewById(R.id.pager2)
        uipages = UIpages(this)
        viewpager.adapter = uipages
    }

    private inner class UIpages(f: Fragment) : FragmentStateAdapter(f) {
        // this UI have const two fragments
        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment {
            if(position == 1)
                return SecondFragment()
            else
                return FirstFragment()
        }
    }

}