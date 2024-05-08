package com.velmurugan.paging3android.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.datn.R
import com.squareup.picasso.Picasso
import com.velmurugan.paging3android.ProductType

class ProductPagerAdapter(private val onClick: ClickListener) :
    PagingDataAdapter<ProductType, ProductPagerAdapter.ProductViewHolder>(ProductComparator) {


    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }


    object ProductComparator : DiffUtil.ItemCallback<ProductType>() {
        override fun areItemsTheSame(oldItem: ProductType, newItem: ProductType): Boolean {
            return oldItem.image_url == newItem.image_url
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
        text.text = product.name
        price.text = product.price.toString()

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
            LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    interface ClickListener {
        fun onClickedItem(itemProduct: ProductType)
        fun onLongItemClick(itemProduct: ProductType)
    }



}