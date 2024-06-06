package com.velmurugan.paging3android.Adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.datn.R
import com.example.datn.utils.Extension.NumberExtensions.toVietnameseCurrency
import com.squareup.picasso.Picasso
import com.velmurugan.paging3android.ProductType

class ProductPagerAdapter(private val onClick: ClickListener) :
    PagingDataAdapter<ProductType, ProductPagerAdapter.ProductViewHolder>(ProductComparator) {


    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }


    object ProductComparator : DiffUtil.ItemCallback<ProductType>() {
        override fun areItemsTheSame(oldItem: ProductType, newItem: ProductType): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ProductType, newItem: ProductType): Boolean {
            return oldItem == newItem
        }
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = getItem(position)!!
        val text = holder.itemView.findViewById<TextView>(R.id.txtNameProduct)
        val price = holder.itemView.findViewById<TextView>(R.id.txtPriceProduct)
        val image = holder.itemView.findViewById<ImageView>(R.id.imageProduct)
        val countFav = holder.itemView.findViewById<TextView>(R.id.txtCountFav)
        val txtCountSold = holder.itemView.findViewById<TextView>(R.id.txtCountSold)
        val txtNg = holder.itemView.findViewById<TextView>(R.id.txtNg)

        if (product.status == 0) {
            txtNg.visibility = View.VISIBLE
            price.visibility = View.GONE
        } else {
            txtNg.visibility = View.GONE
            price.visibility = View.VISIBLE
            price.text = "${product.price}/${product.quantity}"
        }

        countFav.text = " " + product.favorites_count.toString()
        txtCountSold.text = "Đã bán " + product.sold_quantity.toString()
        text.text = product.name

        Picasso.get().load(product.image_url).into(image)

        holder.itemView.setOnClickListener {
            onClick.onClickedItem(product)
        }
        holder.itemView.setOnLongClickListener {
            onClick.onLongItemClick(product)
            true
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.layout_search_item, parent, false)
        return ProductViewHolder(view)
    }

    interface ClickListener {
        fun onClickedItem(itemProduct: ProductType)
        fun onLongItemClick(itemProduct: ProductType)
    }

}