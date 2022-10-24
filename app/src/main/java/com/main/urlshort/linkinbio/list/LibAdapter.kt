package com.main.urlshort.linkinbio.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.main.urlshort.R
import com.main.urlshort.network.DataContent
import com.main.urlshort.network.Respond

class LibAdapter: RecyclerView.Adapter<LibAdapter.ViewHolder>() {

    var onEditLibListener: SetOnEditLibListener? = null

    class ViewHolder(item: View): RecyclerView.ViewHolder(item){
        val libName = item.findViewById<TextView>(R.id.tvLibName)
        var edit = item.findViewById<Button>(R.id.btnCustomize)
    }

    var data = listOf<DataContent>()
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
        holder.edit.setOnClickListener {
            onEditLibListener?.onEditLibListener(item.urlShort.toString())
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }
}

interface SetOnEditLibListener{
    fun onEditLibListener(property: String)
}