package com.main.urlshort.linkinbio.edit

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.graphics.toColorInt
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.main.urlshort.PreviewLibActivity
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
    private var secondaryColor: String = "#000000"
    private var picture: String = ""
    private var pageTitle: String = ""
    private var bio: String = ""
    private var buttonColor: String = ""
    private var textColor: String = ""
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
        binding = FragmentLibEditBinding.inflate(inflater)
        viewModel = ViewModelProvider(this).get(LibViewModel::class.java)
        sharedPreferences = requireActivity().getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE)
        val userid = sharedPreferences.getString("userid", null)
        val args = LibEditFragmentArgs.fromBundle(requireArguments())
        listLink = mutableMapOf()
        property = args.property
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
            backgroundType = "flat-color-text"
        }

        binding.gradientcolor.setOnClickListener {
            binding.flatcolor.setBackgroundResource(R.drawable.flat_noborder)
            binding.gradientcolor.setBackgroundResource(R.drawable.gradient_border)
            backgroundType = "gradient-color-text"
            val random: java.util.Random = java.util.Random()
            val randomNum = random.nextInt(0xffffff + 1)
            val colorCode = String.format("#%06x", randomNum)
            secondaryColor = colorCode
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

        token = sharedPreferences.getString("token", null).toString()

        viewModel.getLibSettings(args.property, userid.toString(), token)

        viewModel.setting.observe(viewLifecycleOwner){
            Log.i("LibSettings Data", it.toString())

            Utils.sharedPreferenceString(sharedPreferences, "token", it.token.toString())
            token = it.token.toString()

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
            val intent = Intent(requireContext(), PreviewLibActivity::class.java)
            intent.putExtra("PROPERTY", args.property)
            startActivity(intent)
//            findNavController().navigate(LibEditFragmentDirections.actionLibEditFragmentToPreviewLibFragmnet())
//            links = mutableListOf()
//            linkText = mutableListOf()
//            for(i in 0 .. listLink.size - 1){
//                links.add(i, listLink.values.elementAt(i).get(0))
//                linkText.add(i, listLink.values.elementAt(i).get(1))
//            }
//            Log.i("Map Links", links.toString())
//            Log.i("Map Links", linkText.toString())
//            Log.i("Map Links", listLink.toString())
//            Log.i("Map Links", listLink.toString())
//            Log.i("Map Links Size", listLink.get(1)?.size.toString())
//            Log.i("Map Links Size", listLink.get(20)?.size.toString())
//            viewModel.editlib(userid!!, links, linkText, property, backgroundType, firstColor, secondaryColor, picture, pageTitle, bio, buttonColor, textColor)
        }

        val customSave = (requireActivity() as AppCompatActivity).supportActionBar?.customView
        val save = customSave?.findViewById<ImageView>(R.id.imgSave)
        save?.setOnClickListener {
            viewModel.respond.removeObservers(viewLifecycleOwner)
            links = mutableListOf()
            linkText = mutableListOf()
            for(i in 0 .. listLink.size - 1){
                links.add(i, listLink.values.elementAt(i).get(0))
                linkText.add(i, listLink.values.elementAt(i).get(1))
            }
            viewModel.editlib(userid.toString(), links, linkText, property, backgroundType, firstColor, secondaryColor, picture, pageTitle, bio, buttonColor, textColor, token)

            viewModel.respond.observe(viewLifecycleOwner){
                Log.i("Edit Lib Respons", it.toString())

                Utils.sharedPreferenceString(sharedPreferences, "token", it?.token.toString())
                token = it?.token.toString()

                if(it?.error?.get(0)?.errorMsg == "Unauthorized"){
                    Toast.makeText(requireContext(), "Trying to access unauthorized property. If this is a mistake, reopen the page.", Toast.LENGTH_LONG).show()
                }

                if(it?.error?.get(0)?.pageTitle != null){
                    binding.tilPageTitle.isErrorEnabled = true
                    binding.tilPageTitle.error = it.error?.get(0)?.pageTitle
                } else {
                    binding.tilPageTitle.isErrorEnabled = false
                }

                if(it?.error?.get(0)?.bio != null){
                    binding.tilBio.isErrorEnabled = true
                    binding.tilBio.error = it.error?.get(0)?.bio
                } else {
                    binding.tilBio.isErrorEnabled = false
                }

                if(it?.error?.get(0)?.links != null){
                    binding.cvAddLink.strokeColor = Color.RED
                    binding.cvAddLink.strokeWidth = 2
                    binding.tvLinkError.text = it.error.get(0).links
                    binding.tvLinkError.visibility = View.VISIBLE
                }

                if(it?.error?.get(0)?.titles != null){
                    binding.cvAddLink.strokeColor = Color.RED
                    binding.cvAddLink.strokeWidth = 2
                    binding.tvTitleError.text = it.error.get(0).titles
                    binding.tvTitleError.visibility = View.VISIBLE
                }

                if(it?.error?.get(0)?.titles == null  && it?.error?.get(0)?.links == null){
                    binding.cvAddLink.strokeColor = 0
                    binding.cvAddLink.strokeWidth = 0
                    binding.tvLinkError.visibility = View.GONE
                    binding.tvTitleError.visibility = View.GONE
                }

                if(it?.error?.get(0)?.backgroundType != null){
                    binding.cvBackgroundColor.strokeColor = Color.RED
                    binding.cvBackgroundColor.strokeWidth = 2
                    binding.tvBackgroundTypeError.text = "Please select between Flat Color or Gradient Color"
                    binding.tvBackgroundTypeError.visibility = View.VISIBLE
                }

                if(it?.error?.get(0)?.firstColor != null){
                    binding.cvBackgroundColor.strokeColor = Color.RED
                    binding.cvBackgroundColor.strokeWidth = 2
                    binding.tvBackgroundColorError.text = "Background color cannot be empty. Please set background color"
                    binding.tvBackgroundColorError.visibility = View.VISIBLE
                }

                if(it?.error?.get(0)?.secondaryColor != null){
                    binding.cvBackgroundColor.strokeColor = Color.RED
                    binding.cvBackgroundColor.strokeWidth = 2
                    binding.tvSecondaryColorError.text = "Error while creating secondary color for gradient. Please reopen the page"
                    binding.tvSecondaryColorError.visibility = View.VISIBLE
                }

                if(it?.error?.get(0)?.backgroundType == null && it?.error?.get(0)?.firstColor == null && it?.error?.get(0)?.secondaryColor == null){
                    binding.cvBackgroundColor.strokeColor = 0
                    binding.cvBackgroundColor.strokeWidth = 0
                    binding.tvBackgroundTypeError.visibility =  View.GONE
                    binding.tvBackgroundColorError.visibility = View.GONE
                    binding.tvSecondaryColorError.visibility = View.GONE
                }

                if(it?.error?.get(0)?.buttoncolor != null){
                    binding.cvButtonTextColor.strokeColor = Color.RED
                    binding.cvButtonTextColor.strokeWidth = 2
                    binding.tvButtonColorError.text = "Button color cannot be empty. Please set button color"
                    binding.tvButtonColorError.visibility = View.VISIBLE
                }

                if(it?.error?.get(0)?.text != null){
                    binding.cvButtonTextColor.strokeColor = Color.RED
                    binding.cvButtonTextColor.strokeWidth = 2
                    binding.tvButtonTextColorError.text = "Button text color cannot be empty. Please set button text color"
                    binding.tvButtonTextColorError.visibility = View.VISIBLE
                }

                if(it?.error?.get(0)?.buttoncolor == null && it?.error?.get(0)?.text == null){
                    binding.cvButtonTextColor.strokeColor = 0
                    binding.cvButtonTextColor.strokeWidth = 0
                    binding.tvButtonColorError.visibility = View.GONE
                    binding.tvButtonTextColorError.visibility = View.GONE

                }

                if(it?.data?.get(0)?.msg == true){
                    Utils.showToast(requireContext(), "Settings saved")
                    val intent = Intent(requireContext(), PreviewLibActivity::class.java)
                    intent.putExtra("PROPERTY", args.property)
                    startActivity(intent)
                }

                if(it?.data?.get(0)?.msg == false){
                    Utils.showToast(requireContext(), "Internal Server Error. Please Try Again")
                }
            }
        }

        binding.btnapplysetting.setOnClickListener {
            val tilUrlpicture = binding.tilImageUrl.editText?.text.toString()
            val tilTitle = binding.tilPageTitle.editText?.text.toString()
            val tilBio = binding.tilBio.editText?.text.toString()

            picture = tilUrlpicture
            pageTitle = tilTitle
            bio = tilBio

            Utils.showToast(requireContext(), "Page title is set")
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