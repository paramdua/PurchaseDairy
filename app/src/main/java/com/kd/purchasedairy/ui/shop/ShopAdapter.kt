package com.kd.purchasedairy.ui.shop

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kd.purchasedairy.R
import com.kd.purchasedairy.ui.product.ProductModel
import kotlinx.android.synthetic.main.item_product.view.*


class ShopAdapter(var list: ArrayList<ShopModel>) :
    RecyclerView.Adapter<ShopAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.name
        val address: TextView = itemView.address
        fun bind(list: ShopModel) {
            title.text = list.name
            address.text = list.address
        }
    }

    fun updateList(list: ArrayList<ShopModel>) {
        this.list = list
        notifyDataSetChanged()
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }
}