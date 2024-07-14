package com.example.datn.adapter

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.datn.R
import com.example.datn.data.dataresult.orders.Order
import com.example.datn.utils.Extension.NumberExtensions.toVietnameseCurrency
import com.squareup.picasso.Picasso

class OrderAdapterSearch(
    private val activity: Activity,
    private val listOrder: MutableList<Order>,
    private val role: Boolean,
    private val onClick: buttonOnClick,
) :
    RecyclerView.Adapter<OrderAdapterSearch.oViewHolder>() {


    class oViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): oViewHolder {
        return oViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.history_item_orders, parent, false))
    }

    override fun getItemCount(): Int {
        return listOrder.size
    }

    override fun onBindViewHolder(holder: oViewHolder, position: Int) {
        val order = listOrder[position]
        val itemProduct = order.items[0]


        val Image = holder.itemView.findViewById<ImageView>(R.id.hImage)
        val productName = holder.itemView.findViewById<TextView>(R.id.hName)
        val quantity = holder.itemView.findViewById<TextView>(R.id.hPhanLoai)
        val Price = holder.itemView.findViewById<TextView>(R.id.hPrice)
        val Count = holder.itemView.findViewById<TextView>(R.id.hCount2)
        val more = holder.itemView.findViewById<TextView>(R.id.hXemThem)
        val textCount = holder.itemView.findViewById<TextView>(R.id.hCOuntText)
        val tong = holder.itemView.findViewById<TextView>(R.id.hCountOrder)
        val btnHuy = holder.itemView.findViewById<Button>(R.id.btnConfirm)
        val btnRating = holder.itemView.findViewById<Button>(R.id.btnRating)

        more.setOnClickListener {
            onClick.moreOnclick(order)
        }
        Log.e("ORDERS","${order.status}")

        when (order.status) {
            0 -> {
                    btnHuy.text = "Chờ xác nhận"
            }

            1 -> {
                    btnHuy.text = "Đang đóng gói"
            }

            2 -> {
                btnHuy.isEnabled = false
                btnHuy.text = "Đang vận chuyển"
            }

            3 -> {
                btnHuy.isEnabled = false
                btnHuy.text = "Đang Giao Hàng"
            }


            4 -> {
                    btnHuy.text = "Đã hoàn thành"
            }

            else -> {
                    btnHuy.text = "Đã hủy"
            }
        }

        btnHuy.setOnClickListener {
            onClick.onClick(order)
        }

        btnRating.setOnClickListener {
            onClick.onRating(order)
        }

        textCount.text = "Tổng tiền(${order.items.size} sản phẩm)"
        Picasso.get().load(itemProduct.product.image_url).into(Image)
        tong.text = order.final_amount.toInt().toVietnameseCurrency()
        productName.text = order.items[0].product.type.name
        quantity.text = "Phân loại: ${itemProduct.product.size}"
        Price.text = "${itemProduct.product.price.toVietnameseCurrency()}"
        Count.text = "x${itemProduct.quantity}"

    }

    interface buttonOnClick {
        fun onClick(itemOrder: Order)

        fun moreOnclick(itemOrder: Order)

        fun onRating(itemOrder: Order)
    }
}