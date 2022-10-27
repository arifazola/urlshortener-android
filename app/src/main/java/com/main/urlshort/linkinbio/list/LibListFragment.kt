package com.main.urlshort.linkinbio.list

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputLayout
import com.main.urlshort.BottomSheetExtension
import com.main.urlshort.R
import com.main.urlshort.SHARED_PREF_KEY
import com.main.urlshort.Utils
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
    private lateinit var adapter: LibAdapter
    private var userid: String = ""

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
        userid = sharedPreferences.getString("userid", null).toString()
        adapter = LibAdapter()
        adapter.onEditLibListener = this
        binding.rvLib.adapter = adapter
        viewModel.getLibData(userid.toString())

        viewModel.respond.observe(viewLifecycleOwner){
            adapter.data = it?.data!!
            Log.i("Lib Data", it.data.toString())
        }

        binding.btnAddLib.setOnClickListener {
            openBottomSheet()
        }
        return binding.root
    }

    private fun openBottomSheet(){
        val bottomSheet = BottomSheetExtension(requireContext())
        val view = bottomSheet.showBottomSheetDialog(requireContext(), R.layout.add_lib, null, null, true, true, layoutInflater)
        val close = view.findViewById<Button>(R.id.btnCloseAdd)
        val createLib = view.findViewById<Button>(R.id.btnCreateLib)
        val backHalf = view.findViewById<TextInputLayout>(R.id.tilLibName)
        close.setOnClickListener {
            bottomSheet.dialog.dismiss()
        }

        createLib.setOnClickListener {
            createLib(backHalf.editText?.text.toString(), userid, backHalf, bottomSheet)
        }
    }

    private fun createLib(backHalf: String, createdBy: String, tilBackHalf: TextInputLayout, bottomSheetExtension: BottomSheetExtension){
        viewModel.respond.removeObservers(viewLifecycleOwner)

        viewModel.createLib("smrt.link/${backHalf}", createdBy)

        viewModel.respond.observe(viewLifecycleOwner){
            Log.i("Respond Create Lib", it.toString())

            if(it?.data?.get(0)?.duplicate != null){
                tilBackHalf.isErrorEnabled = true
                tilBackHalf.error = it?.data?.get(0)?.duplicate
            } else if(it?.data?.get(0)?.msg != "success" && it?.data?.get(0)?.msg != null) {
                tilBackHalf.isErrorEnabled = true
                tilBackHalf.error = it?.data?.get(0)?.msg.toString()
            }else if(it?.data?.get(0)?.msg == "server error" && it?.data?.get(0)?.msg != null){
                Utils.showToast(requireContext(), "Internal Server Error. Please Try Again")
            } else if(it?.error?.get(0)?.backHalf != null){
                tilBackHalf.isErrorEnabled = true
                tilBackHalf.error = it?.error?.get(0)?.backHalf
            } else if(it?.data?.get(0)?.msg == "success"){
                tilBackHalf.isErrorEnabled = false
                viewModel.getLibData(userid.toString())

                viewModel.respond.observe(viewLifecycleOwner){
                    adapter.data = it?.data!!
                }
                adapter.notifyDataSetChanged()
                bottomSheetExtension.dialog.dismiss()
            } else {
                tilBackHalf.isErrorEnabled = false
            }


//            if(it?.data?.get(0)?.duplicate != null){
//                tilBackHalf.isErrorEnabled = true
//                tilBackHalf.error = it?.data?.get(0)?.duplicate
//             } else {
//                tilBackHalf.isErrorEnabled = false
//            }

//            if(it?.data?.get(0)?.msg != "success"){
//                tilBackHalf.isErrorEnabled = true
//                tilBackHalf.error = it?.data?.get(0)?.msg.toString()
//            } else {
//                tilBackHalf.isErrorEnabled = false
//                val adapter = LibAdapter()
//                adapter.notifyDataSetChanged()
//                bottomSheetExtension.dialog.dismiss()
//            }
//
//            if(it?.error?.get(0)?.backHalf != null){
//                tilBackHalf.isErrorEnabled = true
//                tilBackHalf.error = it?.error?.get(0)?.backHalf
//            } else {
//                tilBackHalf.isErrorEnabled = false
//            }
        }
    }

    override fun onEditLibListener(property: String) {
        findNavController().navigate(LibListFragmentDirections.actionLibListFragmentToLibEditFragment(property))
    }
}