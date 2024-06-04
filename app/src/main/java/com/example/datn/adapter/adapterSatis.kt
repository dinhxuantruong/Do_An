package com.example.datn.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.datn.R
import com.example.datn.data.dataresult.OrderItem
import com.example.datn.data.dataresult.orders.Order
import com.example.datn.utils.Extension.NumberExtensions.toVietnameseCurrency
import com.example.datn.view.Admin.AddProductTypeActivity

class adapterSatis(private val activity : Activity, private val listProduct : MutableList<OrderItem>) :
RecyclerView.Adapter<adapterSatis.viewHolder>(){


    class viewHolder(view : View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        return viewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_item_table,parent,false))
    }

    override fun getItemCount(): Int {
       return listProduct.size
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        val item = listProduct[position]

        val name = holder.itemView.findViewById<TextView>(R.id.txtName)
        val count = holder.itemView.findViewById<TextView>(R.id.txtCount)
        val price = holder.itemView.findViewById<TextView>(R.id.txtPrice)

        name.text = item.productName
        count.text = item.quantity.toString()
        price.text = "${(item.price.toInt()*item.quantity).toVietnameseCurrency()}"

    }
}