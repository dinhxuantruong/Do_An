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
import com.example.datn.data.dataresult.Review
import com.squareup.picasso.Picasso

class ratingAdapter(private val activity: Activity, private val listRating: MutableList<Review>) :
    RecyclerView.Adapter<ratingAdapter.vmViewHolder>() {

    class vmViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): vmViewHolder {
        return vmViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.layout_rate, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return listRating.size
    }

    override fun onBindViewHolder(holder: vmViewHolder, position: Int) {
        val itemRate = listRating[position]
        val avatar = holder.itemView.findViewById<ImageView>(R.id.image)
        val name = holder.itemView.findViewById<TextView>(R.id.textView3)
        val timeRate = holder.itemView.findViewById<TextView>(R.id.txtTime)
        val rating = holder.itemView.findViewById<RatingBar>(R.id.ratingBarSendData)
        val comment = holder.itemView.findViewById<TextView>(R.id.txtCommnet)

        Picasso.get().load(itemRate.user.image_url).into(avatar)
        name.text = itemRate.user.name
        timeRate.text = itemRate.human_readable_createAt
        rating.rating = itemRate.rating.toFloat()
        comment.text = itemRate.comment
    }
}