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
import com.example.datn.data.model.categoryfilter

class categoryAdapter(
    private var clickListener: ClickListener,
    private val activity: Activity,
    private val list: MutableList<categoryfilter>
) :
    RecyclerView.Adapter<categoryAdapter.CateGoryViewHolder>() {

    class CateGoryViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CateGoryViewHolder {
        return CateGoryViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.layout_item_grid, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: CateGoryViewHolder, position: Int) {
        val image = holder.itemView.findViewById<ImageView>(R.id.imageCir)
        val textImage = holder.itemView.findViewById<TextView>(R.id.txtView)
        image.setImageResource(list[position].image)
        textImage.text = formatText(list[position].textImage)
        holder.itemView.setOnClickListener {
            clickListener.onClickedItem(list[position])
        }
    }

    interface ClickListener{
        fun onClickedItem(itemBlog : categoryfilter)
    }

    private fun formatText(input: String): String {
        val words = input.split(" ")
        var result = ""
        for (i in words.indices) {
            result += words[i] + " "
            if ((i + 1) % 2 == 0) {
                result += "\n"
            }
        }
        return result.trim()
    }
}