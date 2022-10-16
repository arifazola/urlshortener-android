package com.main.urlshort.linkdetail

import android.media.Image
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.main.urlshort.R
import com.main.urlshort.Utils
import com.main.urlshort.databinding.FragmentLinkDetailBinding
import java.text.SimpleDateFormat

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LinkDetailFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LinkDetailFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var customActionBar: View
    private lateinit var binding: FragmentLinkDetailBinding
    private lateinit var viewModel: LinkDetailViewModel
    private lateinit var edit: ImageView
    private lateinit var save: ImageView

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
        binding = FragmentLinkDetailBinding.inflate(inflater)
        viewModel = ViewModelProvider(this).get(LinkDetailViewModel::class.java)
        val chart = binding.chart
        val entries: MutableList<BarEntry> = mutableListOf()
        val fab = requireActivity().findViewById<FloatingActionButton>(R.id.fabAddLink)
        val args = LinkDetailFragmentArgs.fromBundle(requireArguments())
        customActionBar = (requireActivity() as AppCompatActivity).supportActionBar?.customView!!
        edit = customActionBar.findViewById(R.id.imgEdit)
        save = customActionBar.findViewById(R.id.imgSave)
        binding.tvDate.text = SimpleDateFormat("MMMM dd, yyyy HH:mm").format(args.date.toLong() * 1000L)
        binding.tvOrgUrl.text = args.orgurl
        binding.textView6.text = args.urlshort
        fab.visibility = View.GONE
        binding.etTitleEdit.setText(args.title)
        binding.etBackHalf.setText(args.urlshort)
        entries.add(BarEntry(0f, 10f))
        entries.add(BarEntry(1f, 20f))
        entries.add(BarEntry(2f, 30f))
        entries.add(BarEntry(3f, 40f))
        entries.add(BarEntry(6f, 50f))
        val barDataSet = BarDataSet(entries, "Perkembangan")
        val data = BarData(barDataSet)
        val xAxis = chart.xAxis
        val rightAxis = chart.axisRight
        var date = ArrayList<String>();
        date.add("01-Apr")
        date.add("02-Apr")
        date.add("03-Apr")
        date.add("04-Apr")
        date.add("05-Apr")
        date.add("06-Apr")
        date.add("07-Apr")
        val dateFormatter = AxisDateformatter(date)
        chart.xAxis.valueFormatter = dateFormatter
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        rightAxis.isEnabled = false
        data.barWidth = 0.9f
        chart.data = data
        chart.setFitBars(true)
        chart.invalidate()

        edit?.setOnClickListener {
            editLink()
        }

        save.setOnClickListener {
            saveLink(args.urlid)
        }
        return binding.root
    }

    private fun editLink(){
        val title = customActionBar.findViewById<TextView>(R.id.tvTitleCustom)
        title.text = "Edit"
        edit.visibility = View.GONE
        save.visibility = View.VISIBLE
        binding.materialButton2.visibility = View.GONE
        binding.chart.visibility = View.GONE
        binding.etTitleEdit.visibility = View.VISIBLE
        binding.tvPrefixLink.visibility = View.VISIBLE
        binding.etBackHalf.visibility = View.VISIBLE
    }

    private fun saveLink(urlid: String){
        viewModel.respond.removeObservers(viewLifecycleOwner)

        val title = binding.etTitleEdit.text.toString()
        val backHalf = binding.etBackHalf.text.toString()

        viewModel.editLink(urlid, title, backHalf)

        viewModel.respond.observe(viewLifecycleOwner){
            if(it?.error?.get(0)?.title != null){
                binding.etTitleEdit.error = it.error.get(0).title
            }

            if(it?.error?.get(0)?.backHalf != null){
                binding.etBackHalf.error = it.error.get(0).backHalf
            }

            if(it?.data?.get(0)?.msg == true){
                findNavController().navigate(R.id.linksFragment)
            }

            if(it?.data?.get(0)?.msg == false){
                Utils.showToast(requireContext(), "Server Error. Please try again")
            }

            if(it?.data?.get(0)?.msg == "Duplicate"){
                binding.etBackHalf.error = "Back-half is already taken. Try another one"
            }

            Log.e("Data Link Edit", it.toString())
        }
    }
}

class AxisDateformatter(val values: ArrayList<String>): ValueFormatter(){
    override fun getFormattedValue(value: Float): String {
        return if (value >= 0) {
            if (values.size > value.toInt()) {
                values[value.toInt()]
            } else ""
        } else {
            ""
        }
    }
}