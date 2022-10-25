package com.main.urlshort.linkinbio.edit

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.graphics.toColorInt
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.main.urlshort.R
import com.main.urlshort.SHARED_PREF_KEY
import com.main.urlshort.Utils
import com.main.urlshort.databinding.FragmentLibEditBinding
import com.main.urlshort.linkinbio.LibViewModel
import top.defaults.colorpicker.ColorObserver

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LibEditFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LibEditFragment : Fragment(), ColorObserver{
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentLibEditBinding
    private lateinit var viewModel: LibViewModel
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var listLink: MutableMap<Int, ArrayList<String>>
    private lateinit var links: MutableList<String>
    private lateinit var linkText:  MutableList<String>
    private var initialConstraint = 0
    private var property: String = ""
    private var backgroundType: String = ""
    private var firstColor: String = ""
    private var secondaryColor: String = ""
    private var picture: String = ""
    private var pageTitle: String = ""
    private var bio: String = ""
    private var buttonColor: String = ""
    private var textColor: String = ""


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
        viewModel = ViewModelProvider(this).get(LibViewModel::class.java)
        sharedPreferences = requireActivity().getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE)
        val userid = sharedPreferences.getString("userid", null)
        val args = LibEditFragmentArgs.fromBundle(requireArguments())
        listLink = mutableMapOf()
        property = args.property
        Utils.showToast(requireContext(), "${args.property} $userid")
        initialConstraint = binding.buttonAddLink.id

        binding.colorPicker.subscribe { color, fromUser, shouldPropagate ->
            binding.vPreviewColor.setBackgroundColor(color)
            firstColor = java.lang.String.format(
                "#%06X",
                0xFFFFFF and color
            )
        }

        binding.colorPickerButton.subscribe { color, fromUser, shouldPropagate ->
            binding.vPreviewButtonColor.setBackgroundColor(color)
            buttonColor = java.lang.String.format(
                "#%06X",
                0xFFFFFF and color
            )
        }

        binding.colorPickerText.subscribe { color, fromUser, shouldPropagate ->
            binding.vPreviewTextColor.setBackgroundColor(color)
            textColor = java.lang.String.format(
                "#%06X",
                0xFFFFFF and color
            )
        }

        binding.flatcolor.setOnClickListener {
            binding.flatcolor.setBackgroundResource(R.drawable.flat_border)
            binding.gradientcolor.setBackgroundResource(R.drawable.gradient_noborder)
        }

        binding.gradientcolor.setOnClickListener {
            binding.flatcolor.setBackgroundResource(R.drawable.flat_noborder)
            binding.gradientcolor.setBackgroundResource(R.drawable.gradient_border)
        }

        binding.btnChangeColor.setOnClickListener {
            binding.colorPicker.visibility = View.VISIBLE
            it.visibility = View.GONE
            binding.btnSaveBackground.visibility = View.VISIBLE
            val constraint = binding.clbackgrouncolor
            val constrainSet = ConstraintSet()
            constrainSet.clone(constraint)
            constrainSet.connect(binding.flatcolor.id, ConstraintSet.TOP, binding.btnSaveBackground.id, ConstraintSet.BOTTOM, 16)
            constrainSet.connect(binding.gradientcolor.id, ConstraintSet.TOP, binding.btnSaveBackground.id, ConstraintSet.BOTTOM, 16)
            constrainSet.applyTo(constraint)
        }

        binding.btnSaveBackground.setOnClickListener {
            binding.colorPicker.visibility = View.GONE
            it.visibility = View.GONE
            binding.btnChangeColor.visibility = View.VISIBLE
            val constraint = binding.clbackgrouncolor
            val constrainSet = ConstraintSet()
            constrainSet.clone(constraint)
            constrainSet.connect(binding.flatcolor.id, ConstraintSet.TOP, binding.btnChangeColor.id, ConstraintSet.BOTTOM, 16)
            constrainSet.connect(binding.gradientcolor.id, ConstraintSet.TOP, binding.btnChangeColor.id, ConstraintSet.BOTTOM, 16)
            constrainSet.applyTo(constraint)
        }

        binding.btnChangeButtonColor.setOnClickListener {
            binding.colorPickerButton.visibility = View.VISIBLE
            it.visibility = View.GONE
            binding.btnSetButtonColor.visibility = View.VISIBLE
            val constraint = binding.clButtonSet
            val constrainSet = ConstraintSet()
            constrainSet.clone(constraint)
            constrainSet.connect(binding.tvCurrentTextColor.id, ConstraintSet.TOP, binding.btnSetButtonColor.id, ConstraintSet.BOTTOM, 16)
            constrainSet.applyTo(constraint)
        }

        binding.btnSetButtonColor.setOnClickListener {
            binding.colorPickerButton.visibility = View.GONE
            it.visibility = View.GONE
            binding.btnChangeButtonColor.visibility = View.VISIBLE
            val constraint = binding.clButtonSet
            val constrainSet = ConstraintSet()
            constrainSet.clone(constraint)
            constrainSet.connect(binding.tvCurrentTextColor.id, ConstraintSet.TOP, binding.btnChangeButtonColor.id, ConstraintSet.BOTTOM, 16)
            constrainSet.applyTo(constraint)
        }

        binding.btnChangeTextColor.setOnClickListener {
            binding.colorPickerText.visibility = View.VISIBLE
            it.visibility = View.GONE
            binding.btnSetTextColor.visibility = View.VISIBLE
        }

        binding.btnSetTextColor.setOnClickListener {
            binding.colorPickerText.visibility = View.GONE
            it.visibility = View.GONE
            binding.btnChangeTextColor.visibility = View.VISIBLE
        }

        binding.buttonAddLink.setOnClickListener {
            createLinkForm()
        }

        viewModel.getLibSettings(args.property, userid.toString())

        viewModel.setting.observe(viewLifecycleOwner){
            Log.i("LibSettings Data", it.toString())

            it.data?.get(0)?.firstColor.let {
                binding.colorPicker.setInitialColor(it!!.toColorInt())
                firstColor = it
            }

            it.data?.get(0)?.buttoncolor.let {
                binding.colorPickerButton.setInitialColor(it!!.toColorInt())
                buttonColor = it!!
            }

            it.data?.get(0)?.textColor.let {
                binding.colorPickerText.setInitialColor(it!!.toColorInt())
                textColor = it!!
            }

            it.data?.get(0)?.links?.forEach {
                Log.i("Links List", "${it.link} ${it.libProperty} ${it.text}")
                createLinkForm(it.link, it.text)
                val links = arrayListOf(it.link.toString(), it.text.toString())
                listLink.put(initialConstraint, links)
//                Log.i("Map Links", listLink.toString())
            }

            it.data?.get(0)?.picture.let {
                binding.tilImageUrl.editText?.setText(it)
                picture = it!!
            }

            it.data?.get(0)?.pageTitle.let {
                binding.tilPageTitle.editText?.setText(it)
                pageTitle = it!!
            }

            it.data?.get(0)?.bio.let {
                binding.tilBio.editText?.setText(it)
                bio = it!!
            }

            it.data?.get(0)?.backgroundType.let {
                if(it == "gradient-color-text"){
                    binding.flatcolor.setBackgroundResource(R.drawable.flat_noborder)
                    binding.gradientcolor.setBackgroundResource(R.drawable.gradient_border)
                } else if(it == "flat-color-text") {
                    binding.flatcolor.setBackgroundResource(R.drawable.flat_border)
                    binding.gradientcolor.setBackgroundResource(R.drawable.gradient_noborder)
                } else {
                    binding.flatcolor.setBackgroundResource(R.drawable.flat_noborder)
                    binding.gradientcolor.setBackgroundResource(R.drawable.gradient_noborder)
                }
                backgroundType = it!!
            }

            it.data?.get(0)?.secondaryColor.let {
                secondaryColor = it!!
            }
        }

//        Log.i("Map Links", listLink.toString())

        val seePreview = requireActivity().findViewById<ExtendedFloatingActionButton>(R.id.fabSeePreview)

        seePreview.setOnClickListener {
            links = mutableListOf()
            linkText = mutableListOf()
            for(i in 0 .. listLink.size - 1){
                links.add(i, listLink.values.elementAt(i).get(0))
                linkText.add(i, listLink.values.elementAt(i).get(1))
            }
            Log.i("Map Links", links.toString())
            Log.i("Map Links", linkText.toString())
            Log.i("Map Links", listLink.toString())
//            listLink.replace(1, listLink.values.elementAt(0), listLink.values.)
//            listLink.values.elementAt(0).set(1, "http:pesbuk")
//            listLink.get(6)!!.set(0, "http://wassap")
            Log.i("Map Links", listLink.toString())
            Log.i("Map Links Size", listLink.get(1)?.size.toString())
            Log.i("Map Links Size", listLink.get(20)?.size.toString())
            viewModel.editlib(userid!!, links, linkText, property, backgroundType, firstColor, secondaryColor, picture, pageTitle, bio, buttonColor, textColor)
        }
        return binding.root
    }

    override fun onColor(color: Int, fromUser: Boolean, shouldPropagate: Boolean) {
        binding.vPreviewColor.setBackgroundColor(color)
    }

    fun createLinkForm(url: String? = null, title: String? = null){
        val urlsAndTitles = arrayListOf<String>("", "")
        val constrainLayout = binding.clAddLink
        val constraintSet = ConstraintSet()
        val cardView = CardView(requireContext())
        cardView.id = View.generateViewId()
        val layoutParams = ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(16, 16, 16,0)
        cardView.layoutParams = layoutParams
        constrainLayout.addView(cardView)
        Utils.showToast(requireContext(), initialConstraint.toString())
        constraintSet.clone(constrainLayout)
        constraintSet.connect(cardView.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
        constraintSet.connect(cardView.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
        constraintSet.connect(cardView.id, ConstraintSet.TOP, initialConstraint, ConstraintSet.BOTTOM)
        constraintSet.applyTo(constrainLayout)



        val constraintInner = ConstraintLayout(requireContext())
        constraintInner.id = View.generateViewId()
        val constraintInnerLayoutParams = ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        constraintInner.layoutParams = constraintInnerLayoutParams
        cardView.addView(constraintInner)
        val constraintSetInner = ConstraintSet()

        val editText = EditText(requireContext())
        editText.id = View.generateViewId()
        editText.hint = "URL"
        editText.setText(url)
        editText.setOnFocusChangeListener { view, b ->
            if(view.hasFocus() == false){
                val text = view.findViewById<EditText>(editText.id)
                if(listLink.get(cardView.id)?.size == null){
                    urlsAndTitles.set(0, text.text.toString())
                    listLink.put(cardView.id, urlsAndTitles)
                } else {
                    listLink.get(cardView.id)?.set(0, text.text.toString())
                }
                Utils.showToast(requireContext(), "Lost Focus")
            }
        }

        editText.addTextChangedListener {
            val text = view?.findViewById<EditText>(editText.id)
            if(listLink.get(cardView.id)?.size == null){
                urlsAndTitles.set(0, text?.text.toString())
                listLink.put(cardView.id, urlsAndTitles)
            } else {
                listLink.get(cardView.id)?.set(0, text?.text.toString())
            }
            Utils.showToast(requireContext(), "Typing")
        }

        val editTextLayoutParams = ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        editText.layoutParams = editTextLayoutParams

        val urlTitle = EditText(requireContext())
        urlTitle.id = View.generateViewId()
        urlTitle.hint = "URL Title"
        urlTitle.setText(title)
        urlTitle.setOnFocusChangeListener { view, b ->
            if(view.hasFocus() == false){
                val text = view.findViewById<EditText>(urlTitle.id)
                if(listLink.get(cardView.id)?.size == null){
                    urlsAndTitles.set(1, text?.text.toString())
                    listLink.put(cardView.id, arrayListOf(text.toString(), ""))
                } else {
                    listLink.get(cardView.id)?.set(1, text.text.toString())
                }
                Utils.showToast(requireContext(), "Lost Focus")
            }
        }

        urlTitle.addTextChangedListener {
            val text = view?.findViewById<EditText>(urlTitle.id)
            if(listLink.get(cardView.id)?.size == null){
                urlsAndTitles.set(1, text?.text.toString())
                listLink.put(cardView.id, arrayListOf(text.toString(), ""))
            } else {
                listLink.get(cardView.id)?.set(1, text?.text.toString())
            }
            Utils.showToast(requireContext(), "Typing")
        }
        val titleLayoutParams = ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        urlTitle.layoutParams = titleLayoutParams
        val removeLink = Button(requireContext())
        removeLink.id = View.generateViewId()
        removeLink.text = "Remove Link"
        removeLink.setTextColor(Color.WHITE)
        removeLink.setBackgroundColor(Color.RED)
        val buttonLayoutParam = ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        removeLink.layoutParams = buttonLayoutParam
        constraintInner.addView(editText)
        constraintInner.addView(urlTitle)
        constraintInner.addView(removeLink)
        constraintSetInner.clone(constraintInner)
        constraintSetInner.connect(editText.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
        constraintSetInner.connect(editText.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
        constraintSetInner.connect(editText.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)

        constraintSetInner.connect(urlTitle.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
        constraintSetInner.connect(urlTitle.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
        constraintSetInner.connect(urlTitle.id, ConstraintSet.TOP, editText.id, ConstraintSet.BOTTOM)

        constraintSetInner.connect(removeLink.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
        constraintSetInner.connect(removeLink.id, ConstraintSet.TOP, urlTitle.id, ConstraintSet.BOTTOM)
        constraintSetInner.applyTo(constraintInner)

        removeLink.setOnClickListener{
//            Utils.showToast(requireContext(), cardView.id.toString())
            listLink.remove(cardView.id)
            val selectedCard = cardView.id
            val card = view?.findViewById<CardView>(selectedCard)
            card?.visibility = View.GONE
            Log.i("Map Links", listLink.toString())
        }

        initialConstraint = cardView.id
    }


    /**
     * Potongan kode untuk tambah entry ke map dan hapus berdasarkan key
     * dipake buat ngapus list link
     * keynya bisa id button delete atau id cardview
     * valunya link sama text
     *
     *  val list = listOf(1,2,3)
        val list2 = listOf(4,5,6)
        val map = mutableMapOf(1 to list, 2 to list2)

        print(map.remove(2))
     */
}