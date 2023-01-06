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
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
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
import com.main.urlshort.links.all.FooterAdapter
import com.main.urlshort.network.CurrentLib

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LibListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LibListFragment : Fragment(), SetOnEditLibListener, SetOnLongClickEditLibListener,
    DialogLib.DialogLibListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentLibListBinding
    private lateinit var viewModel: LibViewModel
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var adapter: LibAdapter
    private var userid: String = ""
    private var token = ""
    private var data: MutableList<CurrentLib> = mutableListOf()
    private var totalLink = 0

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
        var page = 1
        data = mutableListOf()
        binding = FragmentLibListBinding.inflate(inflater)
        viewModel = ViewModelProvider(this).get(LibViewModel::class.java)
        viewModel.cancelJob()
        sharedPreferences =
            requireActivity().getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE)
        userid = sharedPreferences.getString("userid", null).toString()
        adapter = LibAdapter()
        val footerAdapter = FooterAdapterLib()
        val concatAdapter = ConcatAdapter(adapter, footerAdapter)
        adapter.onEditLibListener = this
        adapter.onLongClickEditLibListener = this
        binding.rvLib.adapter = concatAdapter
        token = sharedPreferences.getString("token", null).toString()
        viewModel.delete.removeObservers(viewLifecycleOwner)
        viewModel.getLibData(userid.toString(), token, 1)

        adapter.data = data

        viewModel.error.observe(viewLifecycleOwner) {
            if (it == true) {
                Utils.showToast(requireContext(), "Internal Server Error. Please Try Again")
                binding.shimmer.visibility = View.GONE
            }
        }

        viewModel.isCancelled.observe(viewLifecycleOwner) {
            if (it == true) {
                Log.e("Job Cancelled", "Job Cancelled")
            }
        }

        viewModel.loadingData.observe(viewLifecycleOwner) {
            footerAdapter.isLoading = it

            binding.rvLib.setOnScrollChangeListener { _, _, _, _, _ ->
                if (binding.rvLib.canScrollVertically(0) == false) {
                    if (it == false) {
                        if (data.size < totalLink) {
                            viewModel.getLibData(userid, token, page)
                        }
                    }
                }
            }
        }

        viewModel.loadingAnim.observe(viewLifecycleOwner) {
            if (it == true) {
                showLoading()
            } else if (it == false) {
                hideLoading()
            }
        }

        viewModel.respond.removeObservers(viewLifecycleOwner)
        viewModel.respond.observe(viewLifecycleOwner) {
            it?.let {
                binding.shimmer.visibility = View.GONE
                binding.rvLib.visibility = View.VISIBLE
                Utils.sharedPreferenceString(sharedPreferences, "token", it.token.toString())
                token = it?.token.toString()
                if (it.error?.get(0)?.invalidToken != null) {
                    Utils.showToast(requireContext(), it.error.get(0).invalidToken.toString())
                } else {
                    for (i in 0..it.data!!.size - 1) {
                        data.add(CurrentLib(it.data.get(i)?.urlShort.toString(), it.data.get(i).urlID.toString(), it.data.get(i).qrCode.toString()))
                    }

                    totalLink = it.data.get(0).totalLink!!.toInt()
                    adapter.notifyDataSetChanged()
                    page++
                }
                Log.i("Lib Data", it.data.toString())
            }
        }

        binding.btnAddLib.setOnClickListener {
            openBottomSheet()
        }

        viewModel.delete.observe(viewLifecycleOwner) {
            Log.i("Lib Data Sisa", it.toString())
            Utils.sharedPreferenceString(sharedPreferences, "token", it?.token.toString())
            token = it?.token.toString()
//            viewModel.getLibData(userid, "taikucing")
            if (it?.data?.get(0)?.msg != null) {
                findNavController().navigate(LibListFragmentDirections.actionLibListFragmentSelf())
//                viewModel.getLibData(userid, token, 1)
//                viewModel.respond.observe(viewLifecycleOwner){
//                    adapter.data = it!!.data!!
//                    Log.i("Lib Data After Delete", it.toString())
//                }
//                adapter.notifyDataSetChanged()


//                val respond = viewModel.respond.value
//                adapter.data = respond?.data!!
//                adapter.notifyDataSetChanged()
//                Utils.showToast(requireContext(), "This Delete Runs")
            }

            if (it?.error?.get(0)?.errorMsg != null) {
                Utils.showToast(requireContext(), it.error.get(0).errorMsg.toString())
                Log.e("Delete Lib", it.error.get(0).errorMsg.toString())
            }
        }

        Log.i("Recreate", "Recreate")
        return binding.root
    }

    private fun showLoading() {
        binding.btnAddLib.visibility = View.GONE
        binding.shimmer.visibility = View.GONE
        binding.rvLib.visibility = View.GONE
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.loadinganim.visibility = View.VISIBLE
        binding.tvLoading.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        binding.btnAddLib.visibility = View.VISIBLE
        binding.shimmer.visibility = View.VISIBLE
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(true)
        binding.rvLib.visibility = View.GONE
        binding.loadinganim.visibility = View.GONE
        binding.tvLoading.visibility = View.GONE
    }

    private fun openBottomSheet() {
        val bottomSheet = BottomSheetExtension(requireContext())
        val view = bottomSheet.showBottomSheetDialog(
            requireContext(),
            R.layout.add_lib,
            null,
            null,
            true,
            true,
            layoutInflater
        )
        val close = view.findViewById<Button>(R.id.btnCloseAdd)
        val createLib = view.findViewById<Button>(R.id.btnCreateLib)
        val backHalf = view.findViewById<TextInputLayout>(R.id.tilLibName)
        close.setOnClickListener {
            bottomSheet.dialog.dismiss()
        }

        createLib.setOnClickListener {
            createLib(backHalf.editText?.text.toString(), userid, backHalf, bottomSheet)
        }

        viewModel.loading.observe(viewLifecycleOwner) {
            if (it == true) {
                createLib.text = "CREATING LINK-IN-BIO"
                createLib.isEnabled = false
            } else if (it == false) {
                createLib.text = "CREATE LINK-IN-BIO"
                createLib.isEnabled = true
            }
        }
    }

    private fun createLib(
        backHalf: String,
        createdBy: String,
        tilBackHalf: TextInputLayout,
        bottomSheetExtension: BottomSheetExtension
    ) {
        viewModel.create.removeObservers(viewLifecycleOwner)
        val accountType = sharedPreferences.getString("accountType", null)
        viewModel.createLib("smrt.link/${backHalf}", createdBy, accountType.toString(), token)

        viewModel.create.observe(viewLifecycleOwner) {
            Utils.sharedPreferenceString(sharedPreferences, "token", it?.token.toString())
            token = it?.token.toString()

            if (it?.data?.get(0)?.duplicate != null) {
                tilBackHalf.isErrorEnabled = true
                tilBackHalf.error = it?.data?.get(0)?.duplicate
            } else if (it?.data?.get(0)?.msg != "success" && it?.data?.get(0)?.msg != null) {
                tilBackHalf.isErrorEnabled = true
                tilBackHalf.error = it?.data?.get(0)?.msg.toString()
            } else if (it?.data?.get(0)?.msg == "server error" && it?.data?.get(0)?.msg != null) {
                Utils.showToast(requireContext(), "Internal Server Error. Please Try Again")
            } else if (it?.error?.get(0)?.backHalf != null) {
                tilBackHalf.isErrorEnabled = true
                tilBackHalf.error = it?.error?.get(0)?.backHalf
            } else if (it?.data?.get(0)?.msg == "success") {
                tilBackHalf.isErrorEnabled = false
                findNavController().navigate(LibListFragmentDirections.actionLibListFragmentSelf())
//                viewModel.getLibData(userid.toString(), token, 1)

//                viewModel.respond.observe(viewLifecycleOwner){
//                    adapter.data = it?.data!!
//                }
//                adapter.notifyDataSetChanged()
                bottomSheetExtension.dialog.dismiss()
            } else if (it?.error?.get(0)?.limitLib != null) {
                Toast.makeText(
                    requireContext(),
                    it?.error?.get(0)?.limitLib.toString(),
                    Toast.LENGTH_LONG
                ).show()
//                viewModel.getLibData(userid.toString(), token, 1)

//                viewModel.respond.observe(viewLifecycleOwner){
//                    adapter.data = it?.data!!
//                }
//                adapter.notifyDataSetChanged()
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

    override fun onLongClickEditLibListener(property: String, urlid: String, qr: String) {
        val dialog = DialogLib(R.array.lib_option, userid, property, viewModel, adapter, token, urlid, qr)
        dialog.listener = this
        dialog.show(requireFragmentManager(), "LIB Option")
    }

    override fun onButtonZeroClicked(property: String) {
        viewModel.cancelJob()
        findNavController().navigate(
            LibListFragmentDirections.actionLibListFragmentToLibEditFragment(
                property
            )
        )
    }

    override fun onButtonOneClicked(property: String) {

    }

    override fun onButtonTwoClicked(property: String, urlid: String, qr: String) {
        findNavController().navigate(LibListFragmentDirections.actionLibListFragmentToQRFragment(qr, urlid, property))
    }

    override fun onButtonThreeClicked(property: String) {
        viewModel.delete.removeObservers(requireActivity())
        viewModel.deleteLib(userid, property, token)
    }

    override fun onStop() {
        super.onStop()
        viewModel.cancelJob()
    }
}

class DialogLib(
    val arrayItem: Int,
    val userid: String,
    val shortUrl: String,
    val viewModel: LibViewModel,
    val adapter: LibAdapter,
    val token: String,
    val urlid: String,
    val qr: String
) : DialogFragment() {

    lateinit var listener: DialogLibListener

    interface DialogLibListener {
        fun onButtonZeroClicked(property: String) // Edit
        fun onButtonOneClicked(property: String) // Share
        fun onButtonTwoClicked(property: String, urlid: String, qr: String) // QR
        fun onButtonThreeClicked(property: String) // Delete
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        builder.setItems(arrayItem, DialogInterface.OnClickListener { dialogInterface, i ->

            if (i == 0) {
                listener.onButtonZeroClicked(shortUrl)
//                findNavController().navigate(LibListFragmentDirections.actionLibListFragmentToLibEditFragment(shortUrl))
            } else if(i == 1){
                listener.onButtonOneClicked(shortUrl)
            }else if (i == 2) {
                listener.onButtonTwoClicked(shortUrl, urlid, qr)
            } else {
                listener.onButtonThreeClicked(shortUrl)
            }
        })
        return builder.create()
    }
}