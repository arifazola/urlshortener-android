package com.main.urlshort.linkdetail

import android.app.AlertDialog
import android.app.Dialog
import android.content.*
import android.media.Image
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
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
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.main.urlshort.R
import com.main.urlshort.SHARED_PREF_KEY
import com.main.urlshort.Utils
import com.main.urlshort.databinding.FragmentLinkDetailBinding
import com.squareup.moshi.internal.Util
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
class LinkDetailFragment : Fragment(), DialogDelete.DialogDeleteListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var customActionBar: View
    private lateinit var binding: FragmentLinkDetailBinding
    private lateinit var viewModel: LinkDetailViewModel
    private lateinit var clCustomBar: ConstraintLayout
    private lateinit var edit: ImageView
    private lateinit var save: ImageView
    private lateinit var qr: ImageView
    private lateinit var sharedPreferences: SharedPreferences
    private var defaultUrlShort = ""
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
        binding = FragmentLinkDetailBinding.inflate(inflater)
        viewModel = ViewModelProvider(this).get(LinkDetailViewModel::class.java)
        sharedPreferences = requireActivity().getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE)
        token = sharedPreferences.getString("token", null).toString()
//        val fab = requireActivity().findViewById<FloatingActionButton>(R.id.fabAddLink)
        val args = LinkDetailFragmentArgs.fromBundle(requireArguments())
        Utils.showToast(requireContext(), args.qr)
        defaultUrlShort = args.urlshort
        customActionBar = (requireActivity() as AppCompatActivity).supportActionBar?.customView!!
        clCustomBar = customActionBar.findViewById(R.id.clCustomBar)
        edit = customActionBar.findViewById(R.id.imgEdit)
        save = customActionBar.findViewById(R.id.imgSave)
        qr = customActionBar.findViewById(R.id.imgQR)
        binding.tvDate.text = SimpleDateFormat("MMMM dd, yyyy HH:mm").format(args.date.toLong() * 1000L)
        binding.tvOrgUrl.text = args.orgurl
        binding.textView6.text = args.urlshort
//        fab.visibility = View.GONE
        binding.etTitleEdit.setText(args.title)
        binding.etBackHalf.setText(args.urlshort)

        viewModel.loading.observe(viewLifecycleOwner){
            if (it == true){
                showLoading()
                save.visibility = View.GONE
                customActionBar.visibility = View.GONE
                (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
                (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
            } else if(it == false){
                hideLoading()
                save.visibility = View.VISIBLE
                customActionBar.visibility = View.VISIBLE
                (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
                (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(true)
            }
        }

        edit?.setOnClickListener {
            editLink()
        }

        save.setOnClickListener {
            saveLink(args.urlid)
        }

        qr.setOnClickListener {
            findNavController().navigate(LinkDetailFragmentDirections.actionLinkDetailFragmentToQRFragment(args.qr))
        }

        if(args.urlhit.toInt() != 0){
            setChart(args.urlshort)
        }

        binding.materialButton2.setOnClickListener {
            val dialog = DialogShare(R.array.share, args.urlshort)
            dialog.show(requireFragmentManager(), "Share Dialog")
        }

        binding.btnDeleteLink.setOnClickListener {
//            val userid = sharedPreferences.getString("userid", null)
//            deleteLink(userid.toString(), args.urlshort)
            val deleteDialog = DialogDelete()
            deleteDialog.listener = this
            deleteDialog.show(requireFragmentManager(), "DeleteDialog")
        }
        return binding.root
    }

    private fun showLoading(){
        binding.tvDate.visibility = View.GONE
        binding.tvOrgUrl.visibility = View.GONE
        binding.textView6.visibility = View.GONE
        binding.materialButton2.visibility = View.GONE
        binding.chart.visibility = View.GONE
        binding.etTitleEdit.visibility = View.GONE
        binding.tvPrefixLink.visibility = View.GONE
        binding.etBackHalf.visibility = View.GONE
        binding.btnDeleteLink.visibility = View.GONE
        binding.loadinganim.visibility = View.VISIBLE
        binding.tvLoading.visibility = View.VISIBLE
    }

    private fun hideLoading(){
        binding.tvDate.visibility = View.VISIBLE
        binding.tvOrgUrl.visibility = View.VISIBLE
        binding.textView6.visibility = View.VISIBLE
        binding.materialButton2.visibility = View.VISIBLE
        binding.chart.visibility = View.VISIBLE
        binding.etTitleEdit.visibility = View.VISIBLE
        binding.tvPrefixLink.visibility = View.VISIBLE
        binding.etBackHalf.visibility = View.VISIBLE
        binding.btnDeleteLink.visibility = View.VISIBLE
        binding.loadinganim.visibility = View.GONE
        binding.tvLoading.visibility = View.GONE
    }

    private fun deleteLink(userid: String, urlshort: String){
        viewModel.respond.removeObservers(viewLifecycleOwner)
        viewModel.deleteLink(userid, urlshort, token)

        viewModel.respond.observe(viewLifecycleOwner){

            Utils.sharedPreferenceString(sharedPreferences, "token", it?.token.toString())
            if(it?.error?.get(0)?.errorMsg != null){
                Utils.showToast(requireContext(), it.error.get(0).errorMsg.toString())
            }

            if(it?.error?.get(0)?.invalidToken != null){
                Utils.showToast(requireContext(), it.error.get(0).invalidToken.toString())
            }

            if(it?.data?.get(0)?.msg != null){
                findNavController().navigate(LinkDetailFragmentDirections.actionLinkDetailFragmentToLinksFragment2())
                Utils.showToast(requireContext(), "Link deleted")
            }


            Log.i("Data Link Detele", it.toString())
        }
    }

    private fun editLink(){
        val constraint = binding.clLinkDetail
        val constraintSet = ConstraintSet()
        constraintSet.clone(constraint)
        constraintSet.connect(binding.btnDeleteLink.id, ConstraintSet.TOP, binding.etBackHalf.id, ConstraintSet.BOTTOM, 16)
        constraintSet.applyTo(constraint)
        val accountType = sharedPreferences.getString("accountType", null)
        if (accountType == "free") binding.etBackHalf.isEnabled = false else binding.etBackHalf.isEnabled = true
        val constraintSetCustom = ConstraintSet()
        constraintSetCustom.clone(clCustomBar)
        constraintSetCustom.connect(qr.id, ConstraintSet.END, save.id, ConstraintSet.START)
        constraintSetCustom.applyTo(clCustomBar)
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
        val accountType = sharedPreferences.getString("accountType", null)
        val backHalf = if(accountType == "free") defaultUrlShort else binding.etBackHalf.text.toString()
        val userid = sharedPreferences.getString("userid", null)


        viewModel.editLink(urlid, title, backHalf, userid.toString(), accountType.toString(), token)

        viewModel.respond.observe(viewLifecycleOwner){
            Utils.sharedPreferenceString(sharedPreferences, "token", it?.token.toString())
            token = it?.token.toString()

            if(it?.error?.get(0)?.errorMsg == "Unauthorized"){
                Toast.makeText(requireContext(), "Trying to access unauthorized property. If this is a mistake, reopen the page.", Toast.LENGTH_LONG).show()
            }

            if(it?.error?.get(0)?.title != null){
                binding.etTitleEdit.error = it.error.get(0).title
            }

            if(it?.error?.get(0)?.invalidToken != null){
                Utils.showToast(requireContext(), it.error.get(0).invalidToken.toString())
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
            val leftAxis = chart.axisLeft
            val description = chart.description

            val dateFormatter = AxisDateformatter(date)
            chart.xAxis.valueFormatter = dateFormatter
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            rightAxis.isEnabled = false
            description.isEnabled = false
            leftAxis.setDrawZeroLine(false)
            rightAxis.setDrawZeroLine(false)
            data.barWidth = 0.9f
            chart.data = data
            chart.setFitBars(true)
            chart.invalidate()
        }
    }

    override fun onPositiveButtonClicked(dialog: DialogFragment) {
        val userid = sharedPreferences.getString("userid", null)
        binding.tvLoading.text = "Deleting Link"
        deleteLink(userid.toString(), defaultUrlShort)
    }

    override fun onNegativeButtonClicked(dialog: DialogFragment) {
        Utils.showToast(requireContext(), "Cance")
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

class DialogShare(val arrayItem: Int, val shortUrl: String): DialogFragment(){
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        builder.setItems(arrayItem, DialogInterface.OnClickListener { dialogInterface, i ->

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

class DialogDelete: DialogFragment(){
    internal lateinit var listener: DialogDeleteListener

    interface DialogDeleteListener{
        fun onPositiveButtonClicked(dialog: DialogFragment)
        fun onNegativeButtonClicked(dialog: DialogFragment)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())

        builder.setMessage("Are you sure you want to delete this link?")
            .setPositiveButton("Delete", object: DialogInterface.OnClickListener{
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    listener.onPositiveButtonClicked(this@DialogDelete)
                }
            })

            .setNegativeButton("Cancel", object: DialogInterface.OnClickListener{
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    listener.onNegativeButtonClicked(this@DialogDelete)
                }

            })
        return builder.create()
    }
}