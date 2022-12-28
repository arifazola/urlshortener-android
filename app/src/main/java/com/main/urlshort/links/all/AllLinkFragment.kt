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
    private var page = 1
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
        links = mutableListOf()
        binding = FragmentAllLinkBinding.inflate(inflater)
        viewModel = ViewModelProvider(this).get(AllLinksViewModel::class.java)
        sharedPreferences =
            requireActivity().getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE)
        val userid = sharedPreferences.getString("userid", null)
        val token = sharedPreferences.getString("token", null)
        Utils.showToast(requireContext(), "called")
        viewModel.getData(userid.toString(), token.toString(), page)
        val linkAdapter = AllLinksAdapter()
        val loadingAdapter = FooterAdapter()
        val adapter = ConcatAdapter(linkAdapter, loadingAdapter)
        binding.rvLinks.adapter = adapter
        linkAdapter.onLinkSelected = this
        linkAdapter.data = links

        viewModel.loading.observe(viewLifecycleOwner) {
            loadingAdapter.isLoading = it
            loadingAdapter.notifyDataSetChanged()
            isLoading = it


            binding.rvLinks.setOnScrollChangeListener { view, i, i2, i3, i4 ->
                if (binding.rvLinks.canScrollVertically(1) == false) {
                    if(it != true) {
                        if (links.size != totalLink) {
                            viewModel.getData(userid.toString(), token.toString(), page)
                            Log.e("Loading Stats", "Calling Data")
                        }
                    }
                }
            }
            Log.e("Loading Stats", it.toString())
        }

        viewModel.respond.observe(viewLifecycleOwner) {
            binding.shimmer.visibility = View.GONE
            binding.rvLinks.visibility = View.VISIBLE
            Utils.sharedPreferenceString(sharedPreferences, "token", it.token.toString())
            if (it.error?.get(0)?.invalidToken != null) {
                Utils.showToast(requireContext(), it.error.get(0).invalidToken.toString())
            } else {
                if (links.size < it.data!!.get(0).totalLink!!.toInt()) {
                    for (i in 0..it.data!!.size - 1) {
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
                    binding.tvTotalLinks.text = totalLink.toString()
                    linkAdapter.notifyDataSetChanged()
                    page++
                }
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
//        findNavController().navigate(AllLinkFragmentDirections.actionAllLinkFragmentToLinkDetailFragment2(date, title, orgurl, urlShort, urlhit))
        findNavController().navigate(
            LinksFragmentDirections.actionLinksFragmentToLinkDetailFragment(
                date,
                title,
                orgurl,
                urlShort,
                urlhit,
                urlid
            )
        )
    }
}