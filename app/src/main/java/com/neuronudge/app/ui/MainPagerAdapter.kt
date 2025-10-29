package com.neuronudge.app.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.neuronudge.app.ui.losttofound.LostToFoundFragment
import com.neuronudge.app.ui.soundsanctuary.SoundSanctuaryFragment

/**
 * ViewPager adapter for main tabs
 */
class MainPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    
    override fun getItemCount(): Int = 2
    
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> LostToFoundFragment()
            1 -> SoundSanctuaryFragment()
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }
}
