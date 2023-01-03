package com.main.urlshort.links.toplink

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.main.urlshort.R
import com.main.urlshort.SHARED_PREF_KEY
import com.main.urlshort.Utils
import com.main.urlshort.databinding.FragmentTopLinkBinding
import com.main.urlshort.links.LinksFragmentDirections
import com.main.urlshort.links.all.AllLinksViewModel
import com.main.urlshort.links.all.OnLinkSelected
import com.squareup.moshi.internal.Util

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [TopLinkFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TopLinkFragment : Fragment(), OnLinkSelected {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentTopLinkBinding
    private lateinit var viewModel: TopLinkViewModel
    private lateinit var sharedPreferences: SharedPreferences

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
        Log.i("Fragment Create", "Top Link")
        binding = FragmentTopLinkBinding.inflate(inflater)
        viewModel = ViewModelProvider(this).get(TopLinkViewModel::class.java)
        sharedPreferences = requireActivity().getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE)
        val userid = sharedPreferences.getString("userid", null)
        val token = sharedPreferences.getString("token", null)
        viewModel.getData(userid.toString(), token.toString())
        val adapter = TopTenAdapter()
        adapter.onLinkSelected = this
        binding.rvLinks.adapter = adapter

        viewModel.respond.observe(viewLifecycleOwner){
            it?.let {
                binding.shimmer.visibility = View.GONE
                binding.rvLinks.visibility = View.VISIBLE
                Utils.sharedPreferenceString(sharedPreferences, "token", it!!.token.toString())
                Log.i("Data Link Top", it.toString())
                adapter.data = it.data!!
            }
        }
        return binding.root
    }

    override fun setOnLinkSelected(
        urlid: String,
        date: String,
        title: String,
        orgurl: String,
        urlShort: String,
        urlhit: String
    ) {
        findNavController().navigate(LinksFragmentDirections.actionLinksFragmentToLinkDetailFragment(date, title, orgurl, urlShort, urlhit, urlid))
    }

    override fun onStop() {
        super.onStop()
        viewModel.cancelJob()
    }
}