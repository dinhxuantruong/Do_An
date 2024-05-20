package com.example.datn.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.datn.R

class adapterSearch(private val activity: Activity, private var listSearch : MutableList<String>) :
RecyclerView.Adapter<adapterSearch.searchViewHolder>(){

    class searchViewHolder(view : View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): searchViewHolder {
        return searchViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_search,parent,false))
    }

    override fun getItemCount(): Int {
        return listSearch.size
    }

    override fun onBindViewHolder(holder: searchViewHolder, position: Int) {
        val item = listSearch[position]

        val text = holder.itemView.findViewById<TextView>(R.id.txtSearch)
        text.text = item
    }
    fun updateList(newList: MutableList<String>) {
        listSearch = newList
        notifyDataSetChanged()
    }
}