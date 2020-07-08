package com.kd.purchasedairy.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kd.purchasedairy.R
import kotlinx.android.synthetic.main.item_timeline.view.*

class TimelineAdapter(var list: ArrayList<TimelineModel>) :
    RecyclerView.Adapter<TimelineAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val product: TextView = itemView.product
        val shop: TextView = itemView.shop
        val date: TextView = itemView.date
        val price: TextView = itemView.price
    }

    fun updateList(list: ArrayList<TimelineModel>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimelineAdapter.ViewHolder {
        val v: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_timeline, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: TimelineAdapter.ViewHolder, position: Int) {
        holder.product.text = list[position].name
        holder.shop.text = list[position].shop
        holder.price.text = list[position].price.toString()
        holder.date.text = list[position].created.toDate().toString()
    }
}