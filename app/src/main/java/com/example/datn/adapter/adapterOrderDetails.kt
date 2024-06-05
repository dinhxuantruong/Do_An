package com.example.datn.adapter

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.datn.R
import com.example.datn.data.dataresult.Item
import com.example.datn.utils.Extension.NumberExtensions.toVietnameseCurrency
import com.squareup.picasso.Picasso

class adapterOrderDetails(
    private val activity: Activity,private val listProduct: MutableList<Item>
) : RecyclerView.Adapter<adapterOrderDetails.ckViewHolder>() {
    class ckViewHolder(view: View) : RecyclerView.ViewHolder(view)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ckViewHolder {
        return ckViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_checkout, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return listProduct.size
    }


    override fun onBindViewHolder(holder: ckViewHolder, position: Int) {
        val ckItem = listProduct[position]
        Log.e("MAIN12",ckItem.toString())

        val productName = holder.itemView.findViewById<TextView>(R.id.ckName)
        val Price = holder.itemView.findViewById<TextView>(R.id.ckPrice)
        val Image = holder.itemView.findViewById<ImageView>(R.id.imageCheckout)
        val Count = holder.itemView.findViewById<TextView>(R.id.ckCount)
        val quantity = holder.itemView.findViewById<TextView>(R.id.ckPhanLoai)



        productName.text = ckItem.product.type.name
        quantity.text = "Thể loại: ${ckItem.product.size}"
        Picasso.get().load(ckItem.product.image_url).into(Image)
        Price.text = "${ckItem.product.price.toVietnameseCurrency()}"
        Count.text = "Số lượng: ${ckItem.quantity}"
    }
    

}