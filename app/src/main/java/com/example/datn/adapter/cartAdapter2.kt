package com.example.datn.adapter

import android.app.Activity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.datn.R
import com.example.datn.data.dataresult.ItemCartsWithTotal
import com.example.datn.utils.Extention.NumberExtensions.toVietnameseCurrency
import com.squareup.picasso.Picasso

class cartAdapter2(
    private val activity: Activity,
    private val onClick: onClickCart, private val listCart: MutableList<ItemCartsWithTotal>
) : RecyclerView.Adapter<cartAdapter2.cartViewHolder>() {
    class cartViewHolder(view: View) : RecyclerView.ViewHolder(view)

    private var updatingText = false
    private var minusCheck = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): cartViewHolder {
        return cartViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_cart, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return listCart.size
    }


    override fun onBindViewHolder(holder: cartViewHolder, position: Int) {
        val cartItem = listCart[position]
        val checkBox = holder.itemView.findViewById<CheckBox>(R.id.checkBox)
        val productName = holder.itemView.findViewById<TextView>(R.id.cartName)
        val cartPrice = holder.itemView.findViewById<TextView>(R.id.price)
        val cartImage = holder.itemView.findViewById<ImageView>(R.id.cart_image)
        val cartCount = holder.itemView.findViewById<TextView>(R.id.txtQuantity)
        val quantity = holder.itemView.findViewById<TextView>(R.id.cartTl)

        val minus = holder.itemView.findViewById<ImageButton>(R.id.btnDecrease)
        val plus = holder.itemView.findViewById<ImageButton>(R.id.btnIncrease)

        minus.setOnClickListener {
            minusCheck = cartCount.text.toString().toInt() <= 1
            cartCount.clearFocus()
            onClick.minusCart(cartItem, position, minusCheck)
        }

        // Xóa TextWatcher cũ trước khi đặt giá trị mới để tránh trường hợp vòng lặp vô tận

        cartCount.tag?.let {
            cartCount.removeTextChangedListener(it as TextWatcher)
        }

        cartCount.text = cartItem.quantity.toString()

        // Tạo và gán một TextWatcher mới
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updatingText = false
                if (!s.isNullOrEmpty()) {
                    val newQuantity = s.toString().toInt()
                    if (newQuantity <= cartItem.product.stock && newQuantity >= 1) {
                        cartItem.quantity = newQuantity
                        onClick.updateQuantity(cartItem, newQuantity)
                    }
                }
            }


            override fun afterTextChanged(s: Editable?) {
                if (updatingText) return
                val stock = cartItem.product.stock
                val enteredValue = s?.toString() ?: ""
                if (enteredValue.isNotEmpty()) {
                    val newValue = enteredValue.toIntOrNull()
                    if (newValue == null || newValue !in 1..stock) {
                        updatingText = true
                        cartCount.text = stock.toString()
                        Toast.makeText(activity, "Max", Toast.LENGTH_SHORT).show()
                    } else if (enteredValue.length > 2) {
                        updatingText = true
                        cartCount.text = enteredValue.substring(0, 2)
                    }
                }
            }
        }
        cartCount.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                // Gỡ bỏ focus khi EditText mất focus
                cartCount.clearFocus()
            }
        }

        cartCount.addTextChangedListener(textWatcher)
        cartCount.tag = textWatcher

        plus.setOnClickListener {
            cartCount.clearFocus()
            onClick.plusCart(cartItem, position)
        }

        checkBox.setOnCheckedChangeListener(null)
        checkBox.isChecked = cartItem.checkBoxAll

        if (cartItem.checkBoxAll) {
            checkBox.isChecked = true
        }

        checkBox.setOnCheckedChangeListener { _, isChecked ->
            cartItem.checkBoxAll = isChecked
            onClick.checkBoxClick(cartItem)
            checkAllSelected()
        }

        productName.text = cartItem.product.product_type_name
        quantity.text = "Thể loại: ${cartItem.product.size}"
        Picasso.get().load(cartItem.product.image_url).into(cartImage)
        cartPrice.text =
            "${cartItem.product.price.toVietnameseCurrency()}/${cartItem.product.product_quantity}"
//        cartCount.text = cartItem.quantity.toString()
    }

    private fun checkAllSelected() {
        val allChecked = listCart.all { it.checkBoxAll }
        onClick.updateCheckBoxAll(allChecked)
    }

    interface onClickCart {
        fun checkBoxClick(itemCart: ItemCartsWithTotal)
        fun updateCheckBoxAll(isChecked: Boolean)

        fun plusCart(itemCart: ItemCartsWithTotal, pos: Int)

        fun minusCart(itemCart: ItemCartsWithTotal, pos: Int, minusCheck : Boolean)

        fun updateQuantity(itemCart: ItemCartsWithTotal, newQuantity: Int)
    }

    fun checkAllItems(isChecked: Boolean) {
        for (item in listCart) {
            item.checkBoxAll = isChecked
        }
        notifyDataSetChanged()
    }

    fun clearAllCart() {
        listCart.clear()
        notifyDataSetChanged()
    }

    fun updateCartCount(position: Int) {
        val itemCart = listCart[position]
        itemCart.quantity++
        notifyDataSetChanged()
    }

    fun updateCartCountMinus(position: Int) {
        val itemCart = listCart[position]
        itemCart.quantity--
        if (itemCart.quantity == 0) {
            listCart.removeAt(position)
           // minusCheck = false
        }
//        if (itemCart.quantity == 1){
//            minusCheck = true
//        }
        notifyDataSetChanged()
    }
}