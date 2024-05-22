package com.example.datn.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.datn.R
import com.example.datn.data.model.HistoryItemSearch

class adapterSearch(
    private val activity: Activity,
    private var listSearch: MutableList<HistoryItemSearch.SearchHistory>,
    private val onItemDeleteListener: OnItemDeleteListener
) : RecyclerView.Adapter<adapterSearch.searchViewHolder>() {

    class searchViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val text: TextView = view.findViewById(R.id.txtSearch)
        val itemDelete: ImageView = view.findViewById(R.id.deleteItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): searchViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_search, parent, false)
        return searchViewHolder(view)
    }

    override fun getItemCount(): Int = listSearch.size

    override fun onBindViewHolder(holder: searchViewHolder, position: Int) {
        val item = listSearch[position]
        holder.text.text = item.searchtext
        holder.itemDelete.setOnClickListener {
            onItemDeleteListener.onItemDelete(item)
            removeItem(position)
        }
    }

    fun updateList(newList: MutableList<HistoryItemSearch.SearchHistory>) {
        listSearch = newList
        notifyDataSetChanged()
    }

    fun removeItem(position: Int) {
        listSearch.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, listSearch.size)
    }

    interface OnItemDeleteListener {
        fun onItemDelete(item: HistoryItemSearch.SearchHistory)
    }
}
