package com.example.screentime.utils

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.screentime.R
import com.example.screentime.fragments.PendientesFragment
import com.example.screentime.fragments.VistasFragment

class MyPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    override fun getItemCount() = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> PendientesFragment()
            1 -> VistasFragment()
            else -> PendientesFragment()
        }
    }
}
