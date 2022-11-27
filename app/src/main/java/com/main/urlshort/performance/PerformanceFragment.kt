package com.main.urlshort.performance

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.DefaultValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.google.android.material.datepicker.MaterialDatePicker
import com.main.urlshort.R
import com.main.urlshort.SHARED_PREF_KEY
import com.main.urlshort.Utils
import com.main.urlshort.databinding.FragmentPerformanceBinding
import com.main.urlshort.linkdetail.AxisDateformatter
import okhttp3.internal.Util

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PerformanceFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PerformanceFragment : Fragment(), AdapterView.OnItemClickListener,
    OnChartValueSelectedListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentPerformanceBinding
    private lateinit var viewModel: PerformanceViewModel
    private lateinit var sharedPreferences: SharedPreferences
    private var userid: String? = null
    private var accountType: String? = null
    private var selectedLinks: String? = null
    private var dateStart: String? = null
    private var dateEnd: String? = null

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
        binding = FragmentPerformanceBinding.inflate(inflater)
        viewModel = ViewModelProvider(this).get(PerformanceViewModel::class.java)
        sharedPreferences = requireActivity().getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE)

        val items: MutableList<String> = mutableListOf()
        userid = sharedPreferences.getString("userid", null)
        viewModel.getLinkList(userid.toString())
        viewModel.respond.observe(viewLifecycleOwner){
            it.let {
                for(i in 0 .. it.data!!.get(0).linkList!!.size - 1){
                    items.add(it.data!!.get(0).linkList!!.get(i).urlShort)
                }
                val adapter = ArrayAdapter(requireContext(), R.layout.list_item, items)
                (binding.tilSelectLink.editText as AutoCompleteTextView).setAdapter(adapter)
            }
        }



        binding.tietDateRange.setOnFocusChangeListener { view, b ->
            if (view.hasFocus()){
                val datePicker = MaterialDatePicker.Builder.dateRangePicker()
                    .setTitleText("Select Date Range")
                    .build()

                datePicker.show(requireFragmentManager(), "Date Range Picker")

                datePicker.addOnPositiveButtonClickListener {
                    binding.tietDateRange.clearFocus()
                    viewModel.data.removeObservers(viewLifecycleOwner)
                    dateStart = Utils.milsToDate(datePicker.selection!!.first, "MMM dd, YYYY")
                    dateEnd = Utils.milsToDate(datePicker.selection!!.second, "MMM dd, YYYY")
                    binding.tilDateRange.editText!!.setText("${dateStart} to ${dateEnd}")
                    accountType = sharedPreferences.getString("accountType", null)

                    if(selectedLinks == null){
                        binding.tilSelectLink.requestFocus()
                    } else {
                        viewModel.getData(
                            selectedLinks.toString(),
                            dateStart.toString(),
                            dateEnd.toString(),
                            userid.toString(),
                            accountType.toString()
                        )
                    }

                    viewModel.data.observe(viewLifecycleOwner){
                        it?.let {
                            binding.svData.visibility = View.VISIBLE
                            val pieChart = binding.chartUserDevice
                            val pieEntry: MutableList<PieEntry> = mutableListOf()

                            for (i in 0..it.data?.get(0)?.device!!.size - 1) {
                                pieEntry.add(
                                    PieEntry(
                                        it.data.get(0).device!!.get(i).urlid.toFloat(),
                                        it.data.get(0).device!!.get(i).device
                                    )
                                )
                            }

                            val pieSet = PieDataSet(pieEntry, "")
                            pieSet.setColors(
                                intArrayOf(
                                    R.color.pieandroid,
                                    R.color.pieios,
                                    R.color.pieiospc,
                                    R.color.pieotherdevice,
                                    R.color.teal_200
                                ), requireContext()
                            )
                            pieSet.valueFormatter = DefaultValueFormatter(0)
                            val pieData = PieData(pieSet)
                            pieChart.description.isEnabled = false
                            pieChart.data = pieData
                            pieChart.invalidate()

                            val refererChart = binding.chartReferer
                            val refererEntry: MutableList<PieEntry> = mutableListOf()

                            for (i in 0..it.data?.get(0)?.referer!!.size - 1) {
                                refererEntry.add(
                                    PieEntry(
                                        it.data.get(0).referer!!.get(i).urlid.toFloat(),
                                        it.data.get(0).referer!!.get(i).referer
                                    )
                                )
                            }

                            val refererSet = PieDataSet(refererEntry, "")
                            val colorSet = mutableListOf(
                                Color.rgb(193, 37, 82), Color.rgb(255, 102, 0), Color.rgb(245, 199, 0),
                                Color.rgb(106, 150, 31), Color.rgb(179, 100, 53)
                            )
                            if (it.data?.get(0)?.referer!!.size > 5) {
                                for (i in 1..it.data?.get(0)?.referer!!.size - 5) {
                                    val random = java.util.Random()
                                    val newColor = Color.rgb(
                                        random.nextInt(256),
                                        random.nextInt(256),
                                        random.nextInt(256)
                                    )
                                    colorSet.add(newColor)
                                    Log.i("Apakah Iterate", i.toString())
                                }
                                Log.i("Apakah Masuk", colorSet.get(5).toString())
                            }
                            refererSet.setColors(colorSet)
                            refererSet.valueFormatter = DefaultValueFormatter(0)
                            val refererData = PieData(refererSet)
                            refererChart.minAngleForSlices = 20f
                            refererChart.legend.isWordWrapEnabled = true
                            refererChart.legend.orientation = Legend.LegendOrientation.HORIZONTAL
                            refererChart.description.isEnabled = false
                            refererChart.data = refererData

                            refererChart.setOnChartValueSelectedListener(this)
                            refererChart.invalidate()

                            val chartCountry = binding.chartCountry
                            val countrySet: MutableList<BarEntry> = mutableListOf()
                            var country = ArrayList<String>()

                            for (i in 0..it.data?.get(0)?.country!!.size - 1) {
                                countrySet.add(
                                    BarEntry(
                                        i.toFloat(),
                                        it.data.get(0).country!!.get(i).urlid.toFloat()
                                    )
                                )
                                country.add(it.data.get(0).country!!.get(i).country)
                            }

                            if (it.data?.get(0)?.country!!.size < 6) {
//                    val rest = 6 - it.data?.get(0)?.mostVisitedLink!!.size
                                for (i in it.data?.get(0)?.country!!.size..5) {
                                    countrySet.add(BarEntry((i).toFloat(), 0f))
                                    country.add("null")
                                }
                            }

                            val countryDataSet = BarDataSet(countrySet, "Page Views")
                            countryDataSet.valueFormatter = DefaultValueFormatter(0)
                            val countryData = BarData(countryDataSet)
                            val countryXaxis = chartCountry.xAxis
                            val countryRightAxis = chartCountry.axisRight
                            val countryLeftAxis = chartCountry.axisLeft
                            val countryFormatter = AxisDateformatter(country)
                            chartCountry.xAxis.valueFormatter = countryFormatter
                            countryXaxis.position = XAxis.XAxisPosition.BOTTOM
                            countryRightAxis.isEnabled = false
                            chartCountry.description.isEnabled = false
                            countryLeftAxis.setDrawZeroLine(false)
                            countryRightAxis.setDrawZeroLine(false)
                            countryXaxis.labelCount = 4
                            countryData.barWidth = 0.9f
                            chartCountry.data = countryData
                            chartCountry.setFitBars(true)
                            chartCountry.invalidate()

                            val chartCity = binding.chartCity
                            val citySet: MutableList<BarEntry> = mutableListOf()
                            var city = ArrayList<String>()

                            for (i in 0..it.data?.get(0)?.city!!.size - 1) {
                                citySet.add(
                                    BarEntry(
                                        i.toFloat(),
                                        it.data.get(0).city!!.get(i).urlid.toFloat()
                                    )
                                )
                                city.add(it.data.get(0).city!!.get(i).city)
                            }

                            if (it.data?.get(0)?.city!!.size < 6) {
//                    val rest = 6 - it.data?.get(0)?.mostVisitedLink!!.size
                                for (i in it.data?.get(0)?.city!!.size..5) {
                                    citySet.add(BarEntry((i).toFloat(), 0f))
                                    city.add("null")
                                }
                            }

                            val cityDataSet = BarDataSet(citySet, "Page Views")
                            cityDataSet.valueFormatter = DefaultValueFormatter(0)
                            val cityData = BarData(cityDataSet)
                            val cityXaxis = chartCity.xAxis
                            val cityRightAxis = chartCity.axisRight
                            val cityLeftAxis = chartCity.axisLeft
                            val cityFormatter = AxisDateformatter(city)
                            chartCity.xAxis.valueFormatter = cityFormatter
                            cityXaxis.position = XAxis.XAxisPosition.BOTTOM
                            cityRightAxis.isEnabled = false
                            chartCity.description.isEnabled = false
                            cityLeftAxis.setDrawZeroLine(false)
                            cityRightAxis.setDrawZeroLine(false)
                            cityXaxis.labelCount = 4
                            cityData.barWidth = 0.9f
                            chartCity.data = cityData
                            chartCity.setFitBars(true)
                            chartCity.invalidate()
                        }
                    }
                }
            }
        }

        (binding.tilSelectLink.editText as AutoCompleteTextView).onItemClickListener = this
        return binding.root
    }

//    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
//
//    }
//
//    override fun onNothingSelected(p0: AdapterView<*>?) {
//
//    }

    override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        selectedLinks = p0!!.getItemAtPosition(p2).toString()

        if(dateStart == null || dateEnd == null){
            binding.tilDateRange.requestFocus()
        } else {
            val accountType = sharedPreferences.getString("accountType", null)
            viewModel.getData(
                selectedLinks.toString(),
                dateStart.toString(),
                dateEnd.toString(),
                userid.toString(),
                accountType.toString()
            )
        }
    }

    override fun onValueSelected(e: Entry?, h: Highlight?) {
        val pieEntry = e as PieEntry
        binding.chartReferer.centerText = "${pieEntry.label}: ${h?.y?.toInt()} references"
    }

    override fun onNothingSelected() {
        binding.chartReferer.centerText = ""
    }
}