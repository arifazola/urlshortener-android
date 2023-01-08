package com.main.urlshort.dashboard

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.DefaultValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.main.urlshort.*
import com.main.urlshort.databinding.FragmentDashboardBinding
import com.main.urlshort.linkdetail.AxisDateformatter
import com.main.urlshort.network.DataContent

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DashboardFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DashboardFragment : Fragment(), OnChartValueSelectedListener, OnClickMoreInfo {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentDashboardBinding
    private lateinit var viewModel: DashboardViewModel
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
        Utils.showToast(requireContext(), "Created")
        binding = FragmentDashboardBinding.inflate(inflater)
        viewModel = ViewModelProvider(this).get(DashboardViewModel::class.java)
        sharedPreferences =
            requireActivity().getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE)
        val userid = sharedPreferences.getString("userid", null)

        binding.clAlertTop.visibility = View.GONE
        binding.clSliders.visibility = View.GONE
        binding.clMostVisitedLink.visibility = View.GONE
        binding.clUserDevice.visibility = View.GONE
        binding.clCountry.visibility = View.GONE
        binding.clCity.visibility = View.GONE
        binding.clSubsGrowth.visibility = View.GONE
        binding.clReferer.visibility = View.GONE


        setMostVisitedLinkChart(userid!!)

        viewModel.error.observe(viewLifecycleOwner){
            if (it == true){
                Utils.showToast(requireContext(), "Internal server error. Please try again")
            }
        }
        return binding.root
    }

    private fun setMostVisitedLinkChart(userid: String) {
        val accountType = sharedPreferences.getString("accountType", null)
        val token = sharedPreferences.getString("token", null)
        viewModel.getdata(userid, accountType.toString(), token.toString())

        viewModel.respond.removeObservers(viewLifecycleOwner)
        viewModel.respond.observe(viewLifecycleOwner) {
            it?.let {
                binding.clShimmerParent.visibility = View.GONE
                binding.clAlertTop.visibility = View.VISIBLE
                binding.clSliders.visibility = View.VISIBLE
                binding.clMostVisitedLink.visibility = View.VISIBLE
                binding.clUserDevice.visibility = View.VISIBLE
                binding.clCountry.visibility = View.VISIBLE
                binding.clCity.visibility = View.VISIBLE
                binding.clSubsGrowth.visibility = View.VISIBLE
                binding.clReferer.visibility = View.VISIBLE
                Utils.sharedPreferenceString(sharedPreferences, "token", it.token.toString())
                if (it.error != null) {
                    if (it.error!!.get(0).limitDashboard != null) {
                        binding.clAlertTop.visibility = View.VISIBLE
                        binding.clSliders.visibility = View.GONE
                        binding.chart.visibility = View.GONE
                        binding.chartUserDevice.visibility = View.GONE
                        binding.chartCountry.visibility = View.GONE
                        binding.chartCity.visibility = View.GONE
                        binding.chartSubsGrowth.visibility = View.GONE
                        binding.chartReferer.visibility = View.GONE
                        binding.animationView.visibility = View.VISIBLE
                        binding.animationView2.visibility = View.VISIBLE
                        binding.animationView3.visibility = View.VISIBLE
                        binding.animationView4.visibility = View.VISIBLE
                        binding.animationView5.visibility = View.VISIBLE
                        binding.animationView6.visibility = View.VISIBLE

                        val constraintLayout = binding.clParent
                        val constraintSet = ConstraintSet()
                        constraintSet.clone(constraintLayout)
                        constraintSet.connect(
                            binding.clMostVisitedLink.id,
                            ConstraintSet.TOP,
                            binding.clAlertTop.id,
                            ConstraintSet.BOTTOM,
                            8
                        )
                        constraintSet.applyTo(constraintLayout)
                    } else if (it.error.get(0).invalidToken != null) {
                        Utils.showToast(requireContext(), it.error.get(0).invalidToken.toString())
                        binding.clAlertTop.visibility = View.GONE
                        binding.clSliders.visibility = View.GONE
                        binding.chart.visibility = View.GONE
                        binding.chartUserDevice.visibility = View.GONE
                        binding.chartCountry.visibility = View.GONE
                        binding.chartCity.visibility = View.GONE
                        binding.chartSubsGrowth.visibility = View.GONE
                        binding.chartReferer.visibility = View.GONE
                        binding.animationView.visibility = View.GONE
                        binding.animationView2.visibility = View.GONE
                        binding.animationView3.visibility = View.GONE
                        binding.animationView4.visibility = View.GONE
                        binding.animationView5.visibility = View.GONE
                        binding.animationView6.visibility = View.GONE
                    }
                } else if(it.data != null) {
                    binding.clAlertTop.visibility = View.GONE
                    binding.clSliders.visibility = View.VISIBLE
                    binding.chart.visibility = View.VISIBLE
                    binding.chartUserDevice.visibility = View.VISIBLE
                    binding.chartCountry.visibility = View.VISIBLE
                    binding.chartCity.visibility = View.VISIBLE
                    binding.chartSubsGrowth.visibility = View.VISIBLE
                    binding.chartReferer.visibility = View.VISIBLE
                    binding.animationView.visibility = View.GONE
                    binding.animationView2.visibility = View.GONE
                    binding.animationView3.visibility = View.GONE
                    binding.animationView4.visibility = View.GONE
                    binding.animationView5.visibility = View.GONE
                    binding.animationView6.visibility = View.GONE
                    val chart = binding.chart
                    val entries: MutableList<BarEntry> = mutableListOf()
                    var link = ArrayList<String>()
                    for (i in 0..it.data?.get(0)?.mostVisitedLink!!.size - 1) {
                        entries.add(
                            BarEntry(
                                i.toFloat(),
                                it.data.get(0).mostVisitedLink!!.get(i).urlid.toFloat()
                            )
                        )
                        link.add(it.data.get(0).mostVisitedLink!!.get(i).urlshort)
                    }

                    if (it.data?.get(0)?.mostVisitedLink!!.size < 6) {
//                    val rest = 6 - it.data?.get(0)?.mostVisitedLink!!.size
                        for (i in it.data?.get(0)?.mostVisitedLink!!.size..5) {
                            entries.add(BarEntry((i).toFloat(), 0f))
                            link.add("null")
                        }
                    }
                    val barDataSet = BarDataSet(entries, "Page Views")
                    barDataSet.valueFormatter = DefaultValueFormatter(0)
                    val data = BarData(barDataSet)
                    val xAxis = chart.xAxis
                    val rightAxis = chart.axisRight
                    val leftAxis = chart.axisLeft
                    val description = chart.description

                    val dateFormatter = AxisDateformatter(link)
                    chart.xAxis.valueFormatter = dateFormatter
                    xAxis.position = XAxis.XAxisPosition.BOTTOM
                    rightAxis.isEnabled = false
                    description.isEnabled = false
                    leftAxis.setDrawZeroLine(false)
                    rightAxis.setDrawZeroLine(false)
                    xAxis.setDrawGridLines(true)
                    xAxis.labelCount = 4
                    data.barWidth = 0.9f
                    chart.data = data
                    chart.setFitBars(true)
                    chart.invalidate()

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
                        }
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

                    val subsGrowthChart = binding.chartSubsGrowth
                    val subsSet: MutableList<Entry> = mutableListOf()
                    val month = ArrayList<String>()

                    for (i in 0..it.data.get(0).subsGrowth!!.size - 1) {
                        subsSet.add(
                            Entry(
                                i.toFloat(),
                                it.data.get(0).subsGrowth!!.get(i).subsData.toFloat()
                            )
                        )
                        month.add(it.data.get(0).subsGrowth!!.get(i).month)
                    }

                    val subsGrowthSet = LineDataSet(subsSet, "Subscriber Growth")
                    subsGrowthSet.valueFormatter = DefaultValueFormatter(0)
                    subsGrowthSet.setColors(R.color.pieandroid)
                    subsGrowthSet.circleRadius = 5f
                    subsGrowthSet.setCircleColor(R.color.pieandroid)

                    val dateFormatterSubs = AxisDateformatter(month)
                    subsGrowthChart.xAxis.valueFormatter = dateFormatterSubs

                    subsGrowthChart.description.isEnabled = false
                    subsGrowthChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
                    subsGrowthChart.xAxis.labelRotationAngle = 20f
                    subsGrowthChart.data = LineData(subsGrowthSet)
                    subsGrowthChart.animateXY(100, 500)

                    val dataSliders = arrayListOf<String>(
                        it.data!!.get(0).totalLink!!,
                        it.data!!.get(0).totalLib!!,
                        it.data!!.get(0).totalSubs!!
                    )
                    val adapter = SlidersAdapter(dataSliders)
                    adapter.onClickMoreInfo = this
                    binding.viewPager.adapter = adapter
                }
            }
        }
    }

//    override fun onStop() {
//        super.onStop()
//        viewModel.cancelJob()
//    }

    override fun onValueSelected(e: Entry?, h: Highlight?) {
        val pieEntry = e as PieEntry
        binding.chartReferer.centerText = "${pieEntry.label}: ${h?.y?.toInt()} references"
    }

    override fun onNothingSelected() {
        binding.chartReferer.centerText = ""
    }

    override fun onClickListener(position: Int) {
        if (position == 0) {
            findNavController().navigate(R.id.linksFragment)
        } else if (position == 1) {
            findNavController().navigate(R.id.libListFragment)
        } else {
            binding.svParent.smoothScrollTo(0, binding.clSubsGrowth.y.toInt())
        }
    }
}