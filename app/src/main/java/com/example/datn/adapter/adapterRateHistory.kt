package com.example.datn.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.datn.R
import com.example.datn.data.dataresult.ReviewRate
import com.squareup.picasso.Picasso

class adapterRateHistory(private val activity : Activity,
                         private val listRating : MutableList<ReviewRate>,
                         val name : String,
                         val image : String ) : RecyclerView.Adapter<adapterRateHistory.vHolder>() {


    class vHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): vHolder {
        return vHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_rating,parent,false))
    }

    override fun getItemCount(): Int {
        return listRating.size
    }

    override fun onBindViewHolder(holder: vHolder, position: Int) {
        val item = listRating[position]
        val imageUser = holder.itemView.findViewById<ImageView>(R.id.image)
        val nameUser = holder.itemView.findViewById<TextView>(R.id.textView3)
        val timeRate = holder.itemView.findViewById<TextView>(R.id.txtTime)
        val ratingBarSendData = holder.itemView.findViewById<RatingBar>(R.id.ratingBarSendData)
        val txtComment = holder.itemView.findViewById<TextView>(R.id.txtComment)
        val imageType = holder.itemView.findViewById<ImageView>(R.id.imageType)
        val nameProduct = holder.itemView.findViewById<TextView>(R.id.txtNameProduct)

        Picasso.get().load(image).into(imageUser)
        nameUser.text = name
        timeRate.text = item.created_at
        ratingBarSendData.rating = item.rating.toFloat()
        txtComment.text = item.comment
        Picasso.get().load(item.image_url).into(imageType)
        nameProduct.text = item.name
    }
}