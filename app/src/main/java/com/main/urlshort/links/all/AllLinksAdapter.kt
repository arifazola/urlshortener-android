package com.main.urlshort.links.all

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.main.urlshort.R
import com.main.urlshort.network.DataContent
import com.main.urlshort.network.Respond
import java.text.SimpleDateFormat

class AllLinksAdapter: RecyclerView.Adapter<AllLinksAdapter.ViewHolder>() {

    var onLinkSelected: OnLinkSelected? = null
    var data = listOf<DataContent>()
    set(value) {
        field = value
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
        val view = v.inflate(R.layout.rv_all_links, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
//        holder.date.text = item.data!!.get(position).createdDate
        holder.date.text = SimpleDateFormat("MMMM dd, yyyy HH:mm").format(item.createdDate!!.toLong() * 1000L)
//        holder.orgUrl.text = item.data!!.get(position).orgUrl
        holder.orgUrl.text = item.orgUrl
//        holder.shortLink.text = "smrt.link./" + item.data!!.get(position).urlShort
        holder.shortLink.text = "smrt.link./" + item.urlShort
//        holder.urlHit.text = item.data.get(position).urlHit
        holder.urlHit.text = item.urlHit

        holder.clLink.setOnClickListener {
            onLinkSelected?.setOnLinkSelected(item.urlID!!, item.createdDate!!, item.title!!, item.orgUrl!!, item.urlShort!!, item.urlHit!!)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class ViewHolder(item: View): RecyclerView.ViewHolder(item){
        val clLink = item.findViewById<ConstraintLayout>(R.id.clLink)
        val date = item.findViewById<TextView>(R.id.tvDate)
        val orgUrl = item.findViewById<TextView>(R.id.tvOrgUrl)
        val shortLink = item.findViewById<TextView>(R.id.tvShortLink)
        val urlHit = item.findViewById<TextView>(R.id.tvUrlHit)
    }
}

interface OnLinkSelected{
    fun setOnLinkSelected(urlid: String, date: String, title: String, orgurl: String, urlShort: String, urlhit: String)
}