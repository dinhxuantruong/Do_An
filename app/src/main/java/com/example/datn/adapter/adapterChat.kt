package com.example.datn.adapter

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.datn.R
import com.example.datn.data.model.Chat
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class ChatAdapter(
    private val activity: Activity,
    private val mChatList: List<Chat>,
    private val imageUrl: String,
) : RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    private val firebaseUser = FirebaseAuth.getInstance().currentUser

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layout = if (viewType == 1) R.layout.layout_item_right else R.layout.layout_item_left
        return ViewHolder(LayoutInflater.from(activity).inflate(layout, parent, false))
    }

    override fun getItemCount(): Int = mChatList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setIsRecyclable(false)
        val chat = mChatList[position]


        val profileImage = holder.itemView.findViewById<CircleImageView>(R.id.profile_image)
        val showTextMessage = holder.itemView.findViewById<TextView>(R.id.show_text_message)
        val leftImageView = holder.itemView.findViewById<ImageView>(R.id.left_image_view)
        val textSeen = holder.itemView.findViewById<TextView>(R.id.text_seen)
        val rightImageView = holder.itemView.findViewById<ImageView>(R.id.right_image_view)
        Log.e("message", chat.message.toString())

        Picasso.get().load(imageUrl).into(profileImage)

        if (chat.message == "send you an image." && !chat.url.equals("")) {
            if (chat.sender == firebaseUser!!.uid) {
                showTextMessage?.visibility = View.GONE
                rightImageView?.visibility = View.VISIBLE
                Picasso.get().load(chat.url).resize(800, 600).into(rightImageView)
            } else if (chat.sender != firebaseUser.uid) {
                showTextMessage?.visibility = View.GONE
                leftImageView?.visibility = View.VISIBLE
                Picasso.get().load(chat.url).resize(800, 600).into(leftImageView)
            }
        } else {
            showTextMessage?.text = chat.message
        }

        if (position == mChatList.size - 1 && chat.sender.equals(firebaseUser!!.uid)) {
            if (chat.isseen == true) {
                textSeen?.text = "Đã xem"
            } else {
                textSeen?.text = "Đã gửi"
            }
        } else {
            textSeen?.visibility = View.GONE
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (mChatList[position].sender == firebaseUser?.uid) 1 else 0
    }
}
