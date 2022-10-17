package com.main.urlshort


import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.main.urlshort.R

class BottomSheetExtension(val context: Context){

    val dialog = BottomSheetDialog(context)

    fun showBottomSheetDialog(
        context: Context,
        @LayoutRes layout: Int,
        @IdRes textViewToSet: Int? = null,
        textToSet: String? = null,
        fullScreen: Boolean = true,
        expand: Boolean = true,
        layoutInflater: LayoutInflater
    ): View {
        dialog.setOnShowListener {
            val bottomSheet: FrameLayout = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet) ?: return@setOnShowListener
            val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
            if (fullScreen && bottomSheet.layoutParams != null) { showFullScreenBottomSheet(bottomSheet) }

            if (!expand) return@setOnShowListener

            bottomSheet.setBackgroundResource(android.R.color.white)
            expandBottomSheet(bottomSheetBehavior)
        }

        @SuppressLint("InflateParams") // dialog does not need a root view here
        val sheetView = layoutInflater.inflate(layout, null)
        textViewToSet?.also {
            sheetView.findViewById<TextView>(it).text = textToSet
        }

        dialog.setContentView(sheetView)
        dialog.show()
        return sheetView
    }

    private fun showFullScreenBottomSheet(bottomSheet: FrameLayout) {
        val layoutParams = bottomSheet.layoutParams
        layoutParams.height = Resources.getSystem().displayMetrics.heightPixels
        bottomSheet.layoutParams = layoutParams
    }

    private fun expandBottomSheet(bottomSheetBehavior: BottomSheetBehavior<FrameLayout>) {
        bottomSheetBehavior.skipCollapsed = true
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }
}