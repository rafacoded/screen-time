package com.example.screentime.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.screentime.fragments.PendientesFragment
import com.example.screentime.fragments.VistasFragment

class HomePagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> PendientesFragment()
            1 -> VistasFragment()
            else -> PendientesFragment()
        }
    }
}