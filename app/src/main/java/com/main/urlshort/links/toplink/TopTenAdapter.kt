package com.main.urlshort.links.toplink

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.main.urlshort.R
import com.main.urlshort.links.all.OnLinkSelected
import com.main.urlshort.network.DataContent
import java.text.SimpleDateFormat

class TopTenAdapter: RecyclerView.Adapter<TopTenAdapter.ViewHolder>() {

    var onLinkSelected: OnLinkSelected? = null
    var data = listOf<DataContent>()
    set(value) {
        field = value
        notifyDataSetChanged()
    }

     class ViewHolder(item: View): RecyclerView.ViewHolder(item){
         val clLink = item.findViewById<ConstraintLayout>(R.id.clLink)
         val tvDate = item.findViewById<TextView>(R.id.tvDate)
         val tvOrgUrl = item.findViewById<TextView>(R.id.tvOrgUrl)
         val tvShortLink = item.findViewById<TextView>(R.id.tvShortLink)
         val tvUrlHit = item.findViewById<TextView>(R.id.tvUrlHit)
     }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
        val view = v.inflate(R.layout.rv_top_ten, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.tvDate.text = SimpleDateFormat("MMMM dd, yyyy HH:mm").format(item.createdDate!!.toLong() * 1000L)
        holder.tvOrgUrl.text = item.orgUrl
        holder.tvShortLink.text = "shrlnk.my.id/" + item.urlShort
        holder.tvUrlHit.text = item.urlHit

        holder.clLink.setOnClickListener {
            onLinkSelected?.setOnLinkSelected(item.urlID.toString(), item.createdDate.toString(), item.title.toString(), item.orgUrl.toString(), item.urlShort.toString(), item.urlHit.toString())
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }
}