package com.example.datn.adapter

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.datn.R
import com.example.datn.data.DataResult
import com.example.datn.data.Result_slideimages
import com.squareup.picasso.Picasso

class ImageOutAdapter(private val activity: Activity,
                      private val list: List<DataResult>
) : RecyclerView.Adapter<ImageOutAdapter.ImageViewHolder>() {

    inner class ImageViewHolder(view : View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_product_image,parent,false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val image = holder.itemView.findViewById<ImageView>(R.id.prImage)
        val urlImage = list[position].image_url
        Picasso.get().load(urlImage).into(image)
    }
}