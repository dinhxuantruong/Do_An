package com.example.datn.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.datn.R
import com.example.datn.data.dataresult.Product
import com.example.datn.utils.Extension.NumberExtensions.toVietnameseCurrency
import com.squareup.picasso.Picasso

class adapterDataType(private val activity:Activity,private val listData : MutableList<Product>, private val name : String, private val quantity : String) :
RecyclerView.Adapter<adapterDataType.typeViewHolder>(){
    class typeViewHolder(view : View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): typeViewHolder {
        return typeViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_type_product,parent,false))
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    override fun onBindViewHolder(holder: typeViewHolder, position: Int) {
       val item = listData[position]

        val productName = holder.itemView.findViewById<TextView>(R.id.cartName)
        val cartPrice = holder.itemView.findViewById<TextView>(R.id.price)
        val cartImage = holder.itemView.findViewById<ImageView>(R.id.cart_image)
        val tonkho = holder.itemView.findViewById<TextView>(R.id.txtTonKho)
        val txtSize = holder.itemView.findViewById<TextView>(R.id.txtTl)
        val background = holder.itemView.findViewById<LinearLayout>(R.id.layoutTest)

        if (item.stock <=0){
            background.visibility = View.VISIBLE
        }else{
            background.visibility = View.GONE
        }
        productName.text = name
        cartPrice.text = "${item.price.toVietnameseCurrency()}/$quantity"
        Picasso.get().load(item.image_url).into(cartImage)
        tonkho.text = "Tồn kho: ${item.stock}"
        txtSize.text = "Phân loại: ${item.size}"

    }
}