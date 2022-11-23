package com.main.urlshort

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.islamkhsh.CardSliderAdapter

class SlidersAdapter(val dataSliders: ArrayList<String>): CardSliderAdapter<SlidersAdapter.SlidersViewHolder>() {


    override fun bindVH(holder: SlidersViewHolder, position: Int) {
        val item = dataSliders[position]
        holder.total.setText(item)
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
        val title = item.findViewById<TextView>(R.id.tvTotal)
    }
}