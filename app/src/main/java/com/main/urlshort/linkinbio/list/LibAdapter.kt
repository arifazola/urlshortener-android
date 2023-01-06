package com.main.urlshort.linkinbio.list

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.main.urlshort.R
import com.main.urlshort.linkdetail.DialogShare
import com.main.urlshort.links.all.FooterAdapter
import com.main.urlshort.network.CurrentLib
import com.main.urlshort.network.DataContent
import com.main.urlshort.network.Respond

class LibAdapter: RecyclerView.Adapter<LibAdapter.ViewHolder>() {

    var onEditLibListener: SetOnEditLibListener? = null
    var onLongClickEditLibListener: SetOnLongClickEditLibListener? = null

    class ViewHolder(item: View): RecyclerView.ViewHolder(item){
        val libName = item.findViewById<TextView>(R.id.tvLibName)
        val clList = item.findViewById<ConstraintLayout>(R.id.clList)
    }

    var data = listOf<CurrentLib>()
    set(value) {
        field = value
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
        val view = v.inflate(R.layout.rv_lib_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.libName.text = item.urlShort
        holder.clList.setOnClickListener {
            onEditLibListener?.onEditLibListener(item.urlShort)
        }
        holder.clList.setOnLongClickListener {
            onLongClickEditLibListener?.onLongClickEditLibListener(item.urlShort, item.urlid, item.qr)
            return@setOnLongClickListener true
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }
}

class FooterAdapterLib: RecyclerView.Adapter<FooterAdapterLib.FooterLibViewHolder>() {

    var isLoading: Boolean? = true
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    class FooterLibViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        val shimmer = item.findViewById<ShimmerFrameLayout>(R.id.shimmerLib)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FooterLibViewHolder {
        val v = LayoutInflater.from(parent.context)
        val view = v.inflate(R.layout.rv_lib_loading, parent, false)
        return FooterLibViewHolder(view)
    }

    override fun onBindViewHolder(holder: FooterLibViewHolder, position: Int) {
        if (isLoading == true) {
            holder.shimmer.visibility = View.VISIBLE
        } else {
            holder.shimmer.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return if (isLoading == true) {
            1
        } else {
            0
        }
    }
}

interface SetOnEditLibListener{
    fun onEditLibListener(property: String)
}

interface SetOnLongClickEditLibListener{
    fun onLongClickEditLibListener(property: String, qr: String, urlid: String)
}