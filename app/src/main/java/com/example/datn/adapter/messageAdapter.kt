package com.example.datn.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.datn.R
import com.example.datn.data.model.Users
import com.squareup.picasso.Picasso

class messageAdapter(
    private val activity: Activity,
    private val onClick: onClickMessage,
    private val listMes: MutableList<Users>,
    private val isOnline: Boolean
) : RecyclerView.Adapter<messageAdapter.MessageHolder>() {

    class MessageHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageHolder {
        return MessageHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.layout_item_user, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return listMes.size
    }

    override fun onBindViewHolder(holder: MessageHolder, position: Int) {
        val item = listMes[position]
        val image = holder.itemView.findViewById<ImageView>(R.id.profile_image)
        val name = holder.itemView.findViewById<TextView>(R.id.username)
        val isOnline = holder.itemView.findViewById<ImageView>(R.id.online_indicator)

        Picasso.get().load(item.profile).into(image)
        name.text = item.username

        holder.itemView.setOnClickListener {
            onClick.onClick(item)
        }
    }

    interface onClickMessage {
        fun onClick(itemUser: Users)
    }
}