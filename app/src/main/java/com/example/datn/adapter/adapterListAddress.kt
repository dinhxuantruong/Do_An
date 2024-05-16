package com.example.datn.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.datn.R
import com.example.datn.data.dataresult.apiAddress.Addresse

class adapterListAddress(private val activity : Activity,private val isCheckedprivate : checkBoxOnClick,val listAddress : MutableList<Addresse>):
RecyclerView.Adapter<adapterListAddress.aViewholder>(){
    class aViewholder(view : View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): aViewholder {
        return aViewholder(LayoutInflater.from(parent.context).inflate(R.layout.layout_address, parent, false)
        )
    }

    override fun onBindViewHolder(holder: aViewholder, position: Int) {
        val itemAddress = listAddress[position]

        val name = holder.itemView.findViewById<TextView>(R.id.aName)
        val phone = holder.itemView.findViewById<TextView>(R.id.aSdt)
        val address = holder.itemView.findViewById<TextView>(R.id.aAddress)

        val checkBox = holder.itemView.findViewById<CheckBox>(R.id.checkBox)


        // Set checked state based on item's is_default value
        checkBox.isChecked = itemAddress.is_default == 1

        checkBox.setOnCheckedChangeListener(null) // Clear previous listener

        checkBox.setOnClickListener {
            // Uncheck all checkboxes in the list
            for (addressItem in listAddress) {
                addressItem.is_default = 0
            }

            // Check the clicked checkbox and update the itemAddress's is_default
            checkBox.isChecked = true
            itemAddress.is_default = 1

            // Notify the activity/fragment about the checked item
            isCheckedprivate.isCheckedItem(itemAddress)

            // Notify adapter about data change
            notifyDataSetChanged()
        }

        name.text = itemAddress.username
        phone.text = " ${itemAddress.phone}"
        address.text = "${itemAddress.address}, Xã ${itemAddress.ward}," +
                " Huyện ${itemAddress.district}, Tỉnh ${itemAddress.province}"
    }


    override fun getItemCount(): Int {
       return listAddress.size
    }

    fun setCheckedById(itemId: Int) {
        for (address in listAddress) {
            if (address.id == itemId) {
                address.is_default = 1
            } else {
                address.is_default = 0
            }
        }
        notifyDataSetChanged()
    }

    interface checkBoxOnClick{
        fun isCheckedItem(itemAddress : Addresse)
    }
}