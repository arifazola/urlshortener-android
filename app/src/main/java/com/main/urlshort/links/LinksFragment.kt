package com.main.urlshort.links

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.tabs.TabLayout
import com.main.urlshort.R
import com.main.urlshort.Utils
import com.main.urlshort.databinding.FragmentLinksBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LinksFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LinksFragment : Fragment(), TabLayout.OnTabSelectedListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentLinksBinding
    private lateinit var pagerAdapter: LinksPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
//        val drawer = requireActivity().findViewById<DrawerLayout>(R.id.drawerLayout)
//        drawer.closeDrawers()
        binding = FragmentLinksBinding.inflate(inflater)
        setupTablayout()
        setupAdapter()
        return binding.root
    }

    private fun setupTablayout(){
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("All"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Top 10"))
        pagerAdapter = LinksPagerAdapter(childFragmentManager, binding.tabLayout.tabCount)
        binding.tabLayout.addOnTabSelectedListener(this)
    }

    private fun setupAdapter(){
        binding.viewPager.adapter = pagerAdapter
        binding.viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(binding.tabLayout))
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        tab?.position?.let {
            binding.viewPager.currentItem = it
            pagerAdapter.notifyDataSetChanged()
            Log.i("Pager Selected", "Pager Selected")
        }
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {

    }

    override fun onTabReselected(tab: TabLayout.Tab?) {
        Utils.showToast(requireContext(), "Reselected")
    }
}