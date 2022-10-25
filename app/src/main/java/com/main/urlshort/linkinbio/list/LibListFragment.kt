package com.main.urlshort.linkinbio.list

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
import com.main.urlshort.databinding.FragmentLibEditBinding
import com.main.urlshort.databinding.FragmentLibListBinding
import com.main.urlshort.linkinbio.LibViewModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LibListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LibListFragment : Fragment(), SetOnEditLibListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentLibListBinding
    private lateinit var viewModel: LibViewModel
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
        binding = FragmentLibListBinding.inflate(inflater)
        viewModel = ViewModelProvider(this).get(LibViewModel::class.java)
        sharedPreferences = requireActivity().getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE)
        val userid = sharedPreferences.getString("userid", null)
        val adapter = LibAdapter()
        adapter.onEditLibListener = this
        binding.rvLib.adapter = adapter
        viewModel.getLibData(userid.toString())

        viewModel.respond.observe(viewLifecycleOwner){
            adapter.data = it.data!!
            Log.i("Lib Data", it.data.toString())
        }
        return binding.root
    }

    override fun onEditLibListener(property: String) {
        findNavController().navigate(LibListFragmentDirections.actionLibListFragmentToLibEditFragment(property))
    }
}