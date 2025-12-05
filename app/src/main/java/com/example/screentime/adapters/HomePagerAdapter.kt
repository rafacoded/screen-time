package com.example.screentime.adapters

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.screentime.fragments.PendientesFragment
import com.example.screentime.fragments.VistasFragment

class HomePagerAdapter(
    fragment: Fragment,
    private val userId: Int
) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> PendientesFragment().apply {
                arguments = Bundle().apply { putInt("userId", userId)}
            }
            1 -> VistasFragment().apply {
                arguments = Bundle().apply { putInt("userId", userId)}
            }
            else -> Fragment()
        }
    }
}