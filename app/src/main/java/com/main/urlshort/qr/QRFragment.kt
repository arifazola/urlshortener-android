package com.main.urlshort.qr

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.main.urlshort.R
import com.main.urlshort.databinding.FragmentQRBinding
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.URI

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [QRFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class QRFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentQRBinding
    var image: Bitmap? = null

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
        binding = FragmentQRBinding.inflate(inflater)
        val args = QRFragmentArgs.fromBundle(requireArguments())
        val qr = binding.imgQR

        if(args.qr == ""){
            showLoading()
            createQr()
        } else{
            hideLoading()
            Glide.with(requireContext()).load("https://shrlnk.my.id/images/"+args.qr+".jpg")
                .into(qr)



            binding.btnShare.setOnClickListener {
                Glide.with(requireContext()).asBitmap().load("https://shrlnk.my.id/images/"+args.qr+".jpg")
                    .into(object : CustomTarget<Bitmap>() {
                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: Transition<in Bitmap>?
                        ) {
                            val intent = Intent(Intent.ACTION_SEND)
                            intent.setType("image/*")
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            intent.putExtra(Intent.EXTRA_STREAM, getLocalBitmapUri(resource))
                            startActivity(Intent.createChooser(intent, "Share With"))
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {

                        }

                    })
            }
        }
        return binding.root
    }

    private fun createQr(){

    }

    private fun showLoading(){
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.loadinganim.visibility = View.VISIBLE
        binding.tvLoading.visibility = View.VISIBLE
        binding.imgQR.visibility = View.GONE
        binding.tvDestination.visibility = View.GONE
        binding.tvDestinationVal.visibility = View.GONE
        binding.tvShort.visibility = View.GONE
        binding.tvShortVal.visibility = View.GONE
        binding.btnShare.visibility = View.GONE
    }

    private fun hideLoading(){
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(true)
        binding.loadinganim.visibility = View.GONE
        binding.tvLoading.visibility = View.GONE
        binding.imgQR.visibility = View.VISIBLE
        binding.tvDestination.visibility = View.VISIBLE
        binding.tvDestinationVal.visibility = View.VISIBLE
        binding.tvShort.visibility = View.VISIBLE
        binding.tvShortVal.visibility = View.VISIBLE
        binding.btnShare.visibility = View.VISIBLE
    }

    private fun getLocalBitmapUri(bitmap: Bitmap): Uri? {
        var bmpUri: Uri? = null
        try {
            val file = File(
                requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "share_image_" + System.currentTimeMillis() + ".png"
            )
            val out = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out)
            out.close()
            bmpUri = FileProvider.getUriForFile(requireContext(), requireContext().packageName + ".provider", file)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return bmpUri
    }
}