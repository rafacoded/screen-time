package com.example.screentime.utils

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.screentime.R

class MyPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    override fun getItemCount() = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> PendientesFragment()
            1 -> ViendoFragment()
            else -> VistasFragment()
        }
    }
}

class PendientesFragment : Fragment(R.layout.fragment_pendientes)
class ViendoFragment : Fragment(R.layout.fragment_viendo)
class VistasFragment : Fragment(R.layout.fragment_vistas)
