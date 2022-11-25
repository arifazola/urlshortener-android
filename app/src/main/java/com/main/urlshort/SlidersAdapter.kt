package com.main.urlshort

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.github.islamkhsh.CardSliderAdapter
import com.main.urlshort.network.DataContent

class SlidersAdapter(val dataSliders: ArrayList<String>): CardSliderAdapter<SlidersAdapter.SlidersViewHolder>() {

    var onClickMoreInfo: OnClickMoreInfo? = null

    override fun bindVH(holder: SlidersViewHolder, position: Int) {
        if(position == 0){
            holder.total.setText(dataSliders[0])
            holder.title.setText("Total Short Links")
            holder.card.setCardBackgroundColor(Color.parseColor("#17A2B8"))
            holder.moreInfo.setCardBackgroundColor(Color.parseColor("#1591A5"))
        } else if(position == 1){
            holder.total.setText(dataSliders[1])
            holder.title.setText("Total Link-In-Bio")
            holder.card.setCardBackgroundColor(Color.parseColor("#28A745"))
            holder.moreInfo.setCardBackgroundColor(Color.parseColor("#24963E"))
        } else {
            holder.total.setText(dataSliders[2])
            holder.title.setText("Total Subscribers")
            holder.card.setCardBackgroundColor(Color.parseColor("#FFC107"))
            holder.moreInfo.setCardBackgroundColor(Color.parseColor("#E5AD06"))
        }

        holder.moreInfo.setOnClickListener {
            onClickMoreInfo?.onClickListener(position)
        }
    }

    override fun getItemCount(): Int {
        return dataSliders.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SlidersViewHolder {
        val v = LayoutInflater.from(parent.context)
        val view = v.inflate(R.layout.card_slider, parent, false)
        return SlidersViewHolder(view)
    }

    class SlidersViewHolder(item: View): RecyclerView.ViewHolder(item){
        val total = item.findViewById<TextView>(R.id.tvTotal)
        val title = item.findViewById<TextView>(R.id.tvTitle)
        val card = item.findViewById<CardView>(R.id.cvSliders)
        val moreInfo = item.findViewById<CardView>(R.id.cvMoreInfo)
    }
}

interface OnClickMoreInfo{
    fun onClickListener(position: Int)
}