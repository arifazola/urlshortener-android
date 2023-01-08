package com.main.urlshort.links.all

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
import androidx.recyclerview.widget.ConcatAdapter
import com.main.urlshort.R
import com.main.urlshort.SHARED_PREF_KEY
import com.main.urlshort.Utils
import com.main.urlshort.databinding.FragmentAllLinkBinding
import com.main.urlshort.links.LinksFragmentDirections
import com.main.urlshort.network.CurrentLink
import com.main.urlshort.network.Respond

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AllLinkFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AllLinkFragment : Fragment(), OnLinkSelected {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentAllLinkBinding
    private lateinit var viewModel: AllLinksViewModel
    private lateinit var sharedPreferences: SharedPreferences
    private var links: MutableList<CurrentLink> = mutableListOf()
    private var totalLink = 0
    private var isLoading: Boolean? = null

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
        links = mutableListOf()
        binding = FragmentAllLinkBinding.inflate(inflater)
        viewModel = ViewModelProvider(this).get(AllLinksViewModel::class.java)
        viewModel.cancelJob()
        sharedPreferences =
            requireActivity().getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE)
        val userid = sharedPreferences.getString("userid", null)
        var token = sharedPreferences.getString("token", null)
//        val fromDetail = sharedPreferences.getString("from_detail",null)
//        val inLink = sharedPreferences.getString("in_link", null)
//        Utils.showToast(requireContext(), fromDetail.toString())
//        if(fromDetail == null || inLink == null){
//            viewModel.getData(userid.toString(), token.toString(), page)
//        }
//        if (fromDetail == null) viewModel.getData(userid.toString(), token.toString(), page)
        viewModel.getData(userid.toString(), token.toString(), 1)
//        viewModel.getDataTop(userid.toString(), token.toString())
        val linkAdapter = AllLinksAdapter()
        val loadingAdapter = FooterAdapter()
        val adapter = ConcatAdapter(linkAdapter, loadingAdapter)
        binding.rvLinks.adapter = adapter
        linkAdapter.onLinkSelected = this
        linkAdapter.data = links

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

        viewModel.loading.removeObservers(viewLifecycleOwner)
        viewModel.loading.observe(viewLifecycleOwner) {
            loadingAdapter.isLoading = it
            loadingAdapter.notifyDataSetChanged()
            isLoading = it


            binding.rvLinks.setOnScrollChangeListener { view, i, i2, i3, i4 ->
                if (binding.rvLinks.canScrollVertically(1) == false) {
                    if (it == false) {
                        if (links.size != totalLink) {
                            viewModel.getData(userid.toString(), token.toString(), page)
                        }
                    }
                }
            }
        }

        viewModel.respond.removeObservers(viewLifecycleOwner)
        viewModel.respond.observe(viewLifecycleOwner) {
            it?.let {
                binding.shimmer.visibility = View.GONE
                binding.rvLinks.visibility = View.VISIBLE
                Utils.sharedPreferenceString(sharedPreferences, "token", it.token.toString())
                token = it.token.toString()
                if (it.error?.get(0)?.invalidToken != null) {
                    Utils.showToast(requireContext(), it.error.get(0).invalidToken.toString())
                } else if (it.data != null) {
                    for (i in 0..it?.data!!.size - 1) {
                        links.add(
                            CurrentLink(
                                it.data.get(i).urlID.toString(),
                                it.data.get(i).urlShort.toString(),
                                it.data.get(i).orgUrl.toString(),
                                it.data.get(i).qrCode.toString(),
                                it.data.get(i).title.toString(),
                                it.data.get(i).urlHit.toString(),
                                it.data.get(i).createdDate.toString()
                            )
                        )
                    }
                    totalLink = it.data.get(0).totalLink!!.toInt()
                    binding.tvTotalLinks.text = "${totalLink} Links"
                    linkAdapter.notifyDataSetChanged()
                    page++
                }
            }
        }
//        }

        return binding.root
    }

    override fun setOnLinkSelected(
        urlid: String,
        date: String,
        title: String,
        orgurl: String,
        urlShort: String,
        urlhit: String,
        qr: String
    ) {
//        findNavController().navigate(AllLinkFragmentDirections.actionAllLinkFragmentToLinkDetailFragment2(date, title, orgurl, urlShort, urlhit))
//        viewModel.respond.removeObservers(viewLifecycleOwner)
//        viewModel.resetValue()
//        viewModel.resetLoading()
        viewModel.cancelJob()
        findNavController().navigate(
            LinksFragmentDirections.actionLinksFragmentToLinkDetailFragment(
                date,
                title,
                orgurl,
                urlShort,
                urlhit,
                urlid,
                qr
            )
        )
    }

    override fun onStop() {
        super.onStop()
        viewModel.cancelJob()
    }
}