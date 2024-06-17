package com.example.datn.adapter

import android.app.Activity
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.datn.R
import com.example.datn.data.dataresult.Coupon
import com.example.datn.utils.Extension.NumberExtensions.toVietnameseCurrency

class adapterVoucher(
    private val activity: Activity,
    private val listVoucher: MutableList<Coupon>,
    private val onClick : onClickVoucher
) : RecyclerView.Adapter<adapterVoucher.vHolder>() {

    class vHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): vHolder {
        return vHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_coupon, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return listVoucher.size
    }

    override fun onBindViewHolder(holder: vHolder, position: Int) {
        val data = listVoucher[position]
        val nameVoucher = holder.itemView.findViewById<TextView>(R.id.txtNameVoucher)
        val dateVoucher = holder.itemView.findViewById<TextView>(R.id.txtDateVoucher)
        val bgrVoucher = holder.itemView.findViewById<LinearLayout>(R.id.bgrVoucher)

        if (data.discount_type == "fixed") {
            nameVoucher.text = "GIẢM GIÁ ${
                data.discount_value.toInt().toVietnameseCurrency()
            } CHO TẤT CẢ CÁC ĐƠN HÀNG"
        } else if (data.discount_type == "percent") {
            nameVoucher.text = "GIẢM GIÁ ${data.discount_value.toInt()}% CHO TẤT CẢ CÁC ĐƠN HÀNG"
        }
        dateVoucher.text = "Hạn sử dụng: ${data.end_date}"

        holder.itemView.setOnClickListener {
            onClick.onClick(data)
        }

        if (!data.is_valid){
            bgrVoucher.setBackgroundColor(Color.parseColor("#FFE6E6E6"))
        }else{
            bgrVoucher.setBackgroundColor(Color.WHITE)
        }
    }

    interface onClickVoucher{
        fun onClick(voucher : Coupon)
    }

}