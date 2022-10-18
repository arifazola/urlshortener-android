package com.main.urlshort.linkinbio.edit

import android.app.ActionBar.LayoutParams
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.marginStart
import com.main.urlshort.R
import com.main.urlshort.Utils
import com.main.urlshort.databinding.FragmentLibEditBinding
import top.defaults.colorpicker.ColorObserver
import top.defaults.colorpicker.ColorPickerPopup

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LibEditFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LibEditFragment : Fragment(), ColorObserver {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentLibEditBinding

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
        binding = FragmentLibEditBinding.inflate(inflater)

        binding.colorPicker.subscribe { color, fromUser, shouldPropagate ->
            binding.vPreviewColor.setBackgroundColor(color)
        }

        binding.flatcolor.setOnClickListener {
            binding.flatcolor.setBackgroundResource(R.drawable.flat_border)
            binding.gradientcolor.setBackgroundResource(R.drawable.gradient_noborder)
        }

        binding.gradientcolor.setOnClickListener {
            binding.flatcolor.setBackgroundResource(R.drawable.flat_noborder)
            binding.gradientcolor.setBackgroundResource(R.drawable.gradient_border)
        }

        binding.buttonAddLink.setOnClickListener {
            createLinkForm()
        }

        return binding.root
    }

    override fun onColor(color: Int, fromUser: Boolean, shouldPropagate: Boolean) {
        binding.vPreviewColor.setBackgroundColor(color)
    }

    fun createLinkForm(){
        val constrainLayout = binding.clAddLink
        val constraintSet = ConstraintSet()
        constraintSet.clone(constrainLayout)
        val cardView = CardView(requireContext())
        cardView.id = View.generateViewId()
        val layoutParams = ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT)
//        layoutParams.marginStart = 8
//        layoutParams.topMargin = 16
        layoutParams.setMargins(8, 16, 8,0)
        cardView.layoutParams = layoutParams
        constraintSet.connect(cardView.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
        constraintSet.connect(cardView.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
        constraintSet.connect(cardView.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
        constraintSet.applyTo(constrainLayout)
        constrainLayout.addView(cardView)

        val constraintInner = ConstraintLayout(requireContext())
        constraintInner.id = R.id.cl_inner
        val constraintInnerLayoutParams = ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        constraintInner.layoutParams = constraintInnerLayoutParams
        cardView.addView(constraintInner)
        val constraintSetInner = ConstraintSet()
        constraintSetInner.clone(constraintInner)

        val editText = EditText(requireContext())
        editText.id = R.id.et_link
        val editTextLayoutParams = ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        editText.layoutParams = editTextLayoutParams
        constraintSetInner.connect(editText.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
        constraintSetInner.connect(editText.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
        constraintSetInner.connect(editText.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
        constraintSetInner.applyTo(constraintInner)
        constraintInner.addView(editText)
    }
}