package com.main.urlshort.linkdetail

import android.app.AlertDialog
import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
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
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.DefaultValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.main.urlshort.R
import com.main.urlshort.Utils
import com.main.urlshort.databinding.FragmentLinkDetailBinding
import java.text.DecimalFormat
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
//        val fab = requireActivity().findViewById<FloatingActionButton>(R.id.fabAddLink)
        val args = LinkDetailFragmentArgs.fromBundle(requireArguments())
        customActionBar = (requireActivity() as AppCompatActivity).supportActionBar?.customView!!
        edit = customActionBar.findViewById(R.id.imgEdit)
        save = customActionBar.findViewById(R.id.imgSave)
        binding.tvDate.text = SimpleDateFormat("MMMM dd, yyyy HH:mm").format(args.date.toLong() * 1000L)
        binding.tvOrgUrl.text = args.orgurl
        binding.textView6.text = args.urlshort
//        fab.visibility = View.GONE
        binding.etTitleEdit.setText(args.title)
        binding.etBackHalf.setText(args.urlshort)

        edit?.setOnClickListener {
            editLink()
        }

        save.setOnClickListener {
            saveLink(args.urlid)
        }

        if(args.urlhit.toInt() != 0){
            setChart(args.urlshort)
        }

        binding.materialButton2.setOnClickListener {
            val dialog = DialogShare(args.urlshort)
            dialog.show(requireFragmentManager(), "Share Dialog")
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

    private fun setChart(urlshort: String){
        val chart = binding.chart
        val entries: MutableList<BarEntry> = mutableListOf()
        var date = ArrayList<String>();

        viewModel.getStats(urlshort)

        viewModel.stats.observe(viewLifecycleOwner){
            it?.let {
                for(i in 0..it.size - 1){
                    entries.add(BarEntry(i.toFloat(), it.get(i).total!!.toFloat()))
                    date.add(Utils.formatDate(it.get(i).date!!, "yyyy-MM-dd", "MMM dd"))
                }
            }

            val barDataSet = BarDataSet(entries, "Link Visit")
            barDataSet.valueFormatter = DefaultValueFormatter(0)
            val data = BarData(barDataSet)
            val xAxis = chart.xAxis
            val rightAxis = chart.axisRight
            val description = chart.description

            val dateFormatter = AxisDateformatter(date)
            chart.xAxis.valueFormatter = dateFormatter
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            rightAxis.isEnabled = false
            description.isEnabled = false
            data.barWidth = 0.9f
            chart.data = data
            chart.setFitBars(true)
            chart.invalidate()
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

class DialogShare(val shortUrl: String): DialogFragment(){
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        builder.setItems(R.array.share, DialogInterface.OnClickListener { dialogInterface, i ->

            if(i == 1){
                val intent = Intent(Intent.ACTION_SEND)
                intent.setType("text/plain")
                intent.putExtra(Intent.EXTRA_TEXT, "smrt.link/$shortUrl")
                startActivity(Intent.createChooser(intent, "Share With"))
            } else {
                val clipboard: ClipboardManager = requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("copy short", "smrt.link/$shortUrl")
                clipboard.setPrimaryClip(clip)
                Utils.showToast(requireContext(), "Smrtlink copied to clipboard")
            }
        })
        return builder.create()
    }
}