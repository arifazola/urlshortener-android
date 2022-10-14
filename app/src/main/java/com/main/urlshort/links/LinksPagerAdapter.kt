package com.main.urlshort.links

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.main.urlshort.links.all.AllLinkFragment
import com.main.urlshort.links.toplink.TopLinkFragment

class LinksPagerAdapter(fragmentManager: FragmentManager, val page: Int): FragmentPagerAdapter(fragmentManager) {
    override fun getCount(): Int {
        return page
    }

    override fun getItem(position: Int): Fragment {
        when(position){
            0 -> return AllLinkFragment()
            1 -> return TopLinkFragment()
            else -> return AllLinkFragment()
        }
    }
}