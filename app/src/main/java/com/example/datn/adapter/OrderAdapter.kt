package com.example.datn.adapter

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.datn.R
import com.example.datn.data.dataresult.orders.Order
import com.example.datn.utils.Extention.NumberExtensions.toVietnameseCurrency
import com.squareup.picasso.Picasso

class OrderAdapter(private val activity: Activity, private val listOrder : MutableList<Order>) :
RecyclerView.Adapter<OrderAdapter.oViewHolder>(){


    class oViewHolder(view : View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): oViewHolder {
        return oViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.history_item_orders,parent,false))
    }

    override fun getItemCount(): Int {
        return listOrder.size
    }

    override fun onBindViewHolder(holder: oViewHolder, position: Int) {
        val order = listOrder[position]
        val itemProduct = order.items[0]


        val Image = holder.itemView.findViewById<ImageView>(R.id.hImage)
        val productName = holder.itemView.findViewById<TextView>(R.id.hName)
        val quantity = holder.itemView.findViewById<TextView>(R.id.hPhanLoai)
        val Price = holder.itemView.findViewById<TextView>(R.id.hPrice)
        val Count = holder.itemView.findViewById<TextView>(R.id.hCount2)
        val more = holder.itemView.findViewById<TextView>(R.id.hXemThem)
        val textCount = holder.itemView.findViewById<TextView>(R.id.hCOuntText)
        val tong = holder.itemView.findViewById<TextView>(R.id.hCountOrder)
        val btnHuy = holder.itemView.findViewById<Button>(R.id.btnHuy)


        textCount.text = "Tổng tiên(${order.items.size} sản phẩm)"
        Picasso.get().load(itemProduct.product.image_url).into(Image)
        tong.text = order.total.toInt().toVietnameseCurrency()
        productName.text = order.items[0].product.type.name
        quantity.text = "Phân loại: ${itemProduct.product.size}"
        Price.text = "${itemProduct.product.price.toVietnameseCurrency()}"
        Count.text = "x${itemProduct.quantity.toString()}"

    }
}