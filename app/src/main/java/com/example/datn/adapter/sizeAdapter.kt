package com.example.datn.adapter

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.datn.R
import com.example.datn.data.dataresult.Product
import com.squareup.picasso.Picasso

class sizeAdapter(private val onClick :ClickListenerSize, private val activity: Activity,
                  private val listSize : MutableList<Product>) :
RecyclerView.Adapter<sizeAdapter.SizeViewHolder>(){


    class SizeViewHolder(view : View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SizeViewHolder {
        return SizeViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_size,parent,false))
    }

    override fun getItemCount(): Int {
        return  listSize.size
    }
    override fun onBindViewHolder(holder: SizeViewHolder, position: Int) {
        val product = listSize[position]
        val image = holder.itemView.findViewById<ImageView>(R.id.imageSize)
        val nameSize = holder.itemView.findViewById<TextView>(R.id.txtPriceSize)
        nameSize.text = product.size
        Picasso.get().load(product.image_url).into(image)


        Log.e("Test",product.isClicked.toString())

        // Kiểm tra stock của item
        if (product.stock == 0) {
            // Nếu stock = 0, hiển thị trạng thái khác và không cho phép click
            holder.itemView.isClickable = false
            holder.itemView.alpha = 0.5f // Làm mờ item
            // Hiển thị trạng thái khác, ví dụ như thay đổi màu nền
            val backgroundColor = ContextCompat.getColor(holder.itemView.context, R.color.size)
            holder.itemView.setBackgroundColor(backgroundColor)
        } else {
            // Nếu stock > 0, cho phép click và xử lý sự kiện click
            holder.itemView.isClickable = true
            holder.itemView.alpha = 1.0f // Không làm mờ item
            holder.itemView.setOnClickListener {
                // Xử lý khi item được click
                onClick.onClickedItem(product)

                // Nếu item chưa được click trước đó, thì đặt trạng thái click của item mới
                if (!product.isClicked) {
                    listSize.forEach { it.isClicked = false } // Reset trạng thái của các item khác
                    product.isClicked = true
                } else {
                    // Nếu item đã được click trước đó, thì bỏ màu viền
                    product.isClicked = false
                }

                // Thông báo cập nhật item đã thay đổi
                notifyDataSetChanged()
            }

            // Đặt màu viền cho item tùy theo trạng thái click
            val borderDrawable = if (product.isClicked) {
                ContextCompat.getDrawable(holder.itemView.context, R.drawable.size_boder)
            } else {
                ContextCompat.getDrawable(holder.itemView.context, R.drawable.size_boder2)
            }
            holder.itemView.background = borderDrawable
        }
    }
//override fun onBindViewHolder(holder: SizeViewHolder, position: Int) {
//    val product = listSize[position]
//    val image = holder.itemView.findViewById<ImageView>(R.id.imageSize)
//    val nameSize = holder.itemView.findViewById<TextView>(R.id.txtPriceSize)
//    nameSize.text = product.size.toString()
//    Picasso.get().load(product.image_url).into(image)
//
//    holder.itemView.setOnClickListener {
//        onClick.onClickedItem(product)
//
//        // Nếu item chưa được click trước đó, thì đặt trạng thái click của item mới
//        if (!product.isClicked) {
//            listSize.forEach { it.isClicked = false } // Reset trạng thái của các item khác
//            product.isClicked = true
//        } else {
//            // Nếu item đã được click trước đó, thì bỏ màu viền
//            product.isClicked = false
//        }
//
//        // Thông báo cập nhật item đã thay đổi
//        notifyDataSetChanged()
//    }
//
//    // Đặt viền cho item tùy theo trạng thái click
//    val borderDrawable = if (product.isClicked) {
//        ContextCompat.getDrawable(holder.itemView.context, R.drawable.size_boder)
//    } else {
//        null // Không có viền nếu không được click
//    }
//    holder.itemView.background = borderDrawable
//}


    interface ClickListenerSize{
        fun onClickedItem(itemProduct: Product)
    }

}