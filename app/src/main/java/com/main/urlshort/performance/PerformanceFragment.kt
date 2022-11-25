package com.main.urlshort.performance

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import com.google.android.material.datepicker.MaterialDatePicker
import com.main.urlshort.R
import com.main.urlshort.Utils
import com.main.urlshort.databinding.FragmentPerformanceBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PerformanceFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PerformanceFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentPerformanceBinding

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

        val items = listOf("Sprite", "One", "Two")
        val adapter = ArrayAdapter(requireContext(), R.layout.list_item, items)
        (binding.tilSelectLink.editText as AutoCompleteTextView).setAdapter(adapter)

        binding.tietDateRange.setOnFocusChangeListener { view, b ->
            if (view.hasFocus()){
                val datePicker = MaterialDatePicker.Builder.dateRangePicker()
                    .setTitleText("Select Date Range")
                    .build()

                datePicker.show(requireFragmentManager(), "Date Range Picker")

                datePicker.addOnPositiveButtonClickListener {
                    binding.tilDateRange.editText!!.setText("${Utils.milsToDate(datePicker.selection!!.first, "MMM dd, YYYY")} to ${Utils.milsToDate(datePicker.selection!!.second, "MMM dd, YYYY")}")
                }
            }
        }
        return binding.root
    }
}