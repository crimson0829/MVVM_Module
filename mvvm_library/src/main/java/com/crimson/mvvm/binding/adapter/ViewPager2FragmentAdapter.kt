package com.crimson.mvvm.binding.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

/**
 * @author crimson
 * @date   2020-12-02
 *
 */
class ViewPager2FragmentAdapter(fa: FragmentActivity, val fragments: MutableList<Fragment>) :
    FragmentStateAdapter(fa) {


    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]

    }

}