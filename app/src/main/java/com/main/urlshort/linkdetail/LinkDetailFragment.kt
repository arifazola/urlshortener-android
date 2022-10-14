package com.main.urlshort.linkdetail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.main.urlshort.R
import com.main.urlshort.databinding.FragmentLinkDetailBinding

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
    private lateinit var binding: FragmentLinkDetailBinding

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
        val chart = binding.chart
        val entries: MutableList<BarEntry> = mutableListOf()
        val fab = requireActivity().findViewById<FloatingActionButton>(R.id.fabAddLink)
        fab.visibility = View.GONE
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
        return binding.root
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