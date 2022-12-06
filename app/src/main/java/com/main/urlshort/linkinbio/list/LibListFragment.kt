package com.main.urlshort.linkinbio.list

import android.app.AlertDialog
import android.app.Dialog
import android.content.*
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputLayout
import com.main.urlshort.BottomSheetExtension
import com.main.urlshort.R
import com.main.urlshort.SHARED_PREF_KEY
import com.main.urlshort.Utils
import com.main.urlshort.databinding.FragmentLibEditBinding
import com.main.urlshort.databinding.FragmentLibListBinding
import com.main.urlshort.linkdetail.DialogShare
import com.main.urlshort.linkdetail.LinkDetailFragmentDirections
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
class LibListFragment : Fragment(), SetOnEditLibListener, SetOnLongClickEditLibListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentLibListBinding
    private lateinit var viewModel: LibViewModel
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var adapter: LibAdapter
    private var userid: String = ""
    private var token = ""

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
        adapter.onLongClickEditLibListener = this
        binding.rvLib.adapter = adapter
        token = sharedPreferences.getString("token", null).toString()
        viewModel.delete.removeObservers(viewLifecycleOwner)
        viewModel.getLibData(userid.toString(), token)

        viewModel.respond.observe(viewLifecycleOwner){
            binding.shimmer.visibility = View.GONE
            binding.rvLib.visibility = View.VISIBLE
            Utils.sharedPreferenceString(sharedPreferences, "token", it?.token.toString())
            token = it?.token.toString()
            adapter.data = it?.data!!
            Log.i("Lib Data", it.data.toString())
        }

        binding.btnAddLib.setOnClickListener {
            openBottomSheet()
        }

        viewModel.delete.observe(viewLifecycleOwner){
            Log.i("Lib Data Sisa", it.toString())
            Utils.sharedPreferenceString(sharedPreferences, "token", it?.token.toString())
            token = it?.token.toString()
//            viewModel.getLibData(userid, "taikucing")
            if(it?.data?.get(0)?.msg != null){
                viewModel.getLibData(userid, token)
                viewModel.respond.observe(viewLifecycleOwner){
                    adapter.data = it!!.data!!
                    Log.i("Lib Data After Delete", it.toString())
                }
                adapter.notifyDataSetChanged()
//                val respond = viewModel.respond.value
//                adapter.data = respond?.data!!
//                adapter.notifyDataSetChanged()
//                Utils.showToast(requireContext(), "This Delete Runs")
            }

            if(it?.error?.get(0)?.errorMsg != null){
                Utils.showToast(requireContext(), it.error.get(0).errorMsg.toString())
                Log.e("Delete Lib", it.error.get(0).errorMsg.toString())
            }
        }

        Log.i("Recreate", "Recreate")
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
        viewModel.create.removeObservers(viewLifecycleOwner)
        val accountType = sharedPreferences.getString("accountType", null)
        viewModel.createLib("smrt.link/${backHalf}", createdBy, accountType.toString(), token)

        viewModel.create.observe(viewLifecycleOwner){
            Utils.sharedPreferenceString(sharedPreferences, "token", it?.token.toString())
            token = it?.token.toString()

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
                viewModel.getLibData(userid.toString(), token)

                viewModel.respond.observe(viewLifecycleOwner){
                    adapter.data = it?.data!!
                }
                adapter.notifyDataSetChanged()
                bottomSheetExtension.dialog.dismiss()
            } else if(it?.error?.get(0)?.limitLib != null){
                Toast.makeText(requireContext(), it?.error?.get(0)?.limitLib.toString(), Toast.LENGTH_LONG).show()
                viewModel.getLibData(userid.toString(), token)

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
        Utils.showToast(requireContext(), "Click and hold to open options")
    }

    override fun onLongClickEditLibListener(property: String) {
        val dialog = DialogLib(R.array.lib_option, userid, property, viewModel, adapter, token)
        dialog.show(requireFragmentManager(), "LIB Option")
        Log.i("Long Click", "Long Click")
        Utils.showToast(requireContext(), token)
    }

    class DialogLib(val arrayItem: Int, val userid: String,  val shortUrl: String, val viewModel: LibViewModel, val adapter: LibAdapter, val token: String): DialogFragment(){
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val builder = AlertDialog.Builder(requireContext())
            builder.setItems(arrayItem, DialogInterface.OnClickListener { dialogInterface, i ->

                if(i == 0){
                    findNavController().navigate(LibListFragmentDirections.actionLibListFragmentToLibEditFragment(shortUrl))
                } else {
                    Utils.showToast(requireContext(), "Option Delete Selected")
                    viewModel.delete.removeObservers(requireActivity())
                    viewModel.deleteLib(userid, shortUrl, token)
                }
            })
            return builder.create()
        }
    }
}