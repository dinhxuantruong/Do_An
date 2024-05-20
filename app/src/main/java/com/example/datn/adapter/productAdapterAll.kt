package com.example.datn.adapter

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.datn.R

import com.example.datn.data.dataresult.ProductTypeX
import com.example.datn.utils.Extension.NumberExtensions.toVietnameseCurrency
import com.squareup.picasso.Picasso


class productAdapterAll(
    private val activity: Activity,
    private val onClick: productAdapter.ClickListener2,
    private var listProduct: List<ProductTypeX>
) : RecyclerView.Adapter<productAdapterAll.ProductViewHolder>() {

    private val diffUtilCallback = object : DiffUtil.ItemCallback<ProductTypeX>() {
        override fun areItemsTheSame(oldItem: ProductTypeX, newItem: ProductTypeX): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ProductTypeX, newItem: ProductTypeX): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffUtilCallback)

    init {
        differ.submitList(listProduct) // Gán danh sách dữ liệu cho differ trong constructor
    }

    inner class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        return ProductViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.layout_search_item, parent, false)
        )
    }

    override fun getItemCount(): Int {
        Log.e("Main", differ.currentList.size.toString()) // Di chuyển Log ra ngoài hàm này
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val products = differ.currentList[position]
        val imageProduct = holder.itemView.findViewById<ImageView>(R.id.imageProduct)
        val name = holder.itemView.findViewById<TextView>(R.id.txtNameProduct)
        val price = holder.itemView.findViewById<TextView>(R.id.txtPriceProduct)

        Picasso.get().load(products.image_url).into(imageProduct)
//        Glide.with(activity).load(products.image_url).into(imageProduct)
        name.text = products.name
        price.text = "${products.price.toVietnameseCurrency()}/${products.quantity}"

        holder.itemView.setOnClickListener {
            onClick.onClickedItem(differ.currentList[position])
        }
    }

    interface ClickListener2 {
        fun onClickedItem(itemProduct: ProductTypeX)
    }



}