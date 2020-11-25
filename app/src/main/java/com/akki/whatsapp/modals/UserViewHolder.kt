package com.akki.whatsapp.modals

import android.view.TextureView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.akki.whatsapp.R
import kotlinx.android.synthetic.main.card_layout.view.*

class UserViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
    val unreadMessages = itemView.findViewById<TextView>(R.id.unreadMessages)
    val lastChatDate = itemView.findViewById<TextView>(R.id.lastChatDate)
    var chatPersonName = itemView.findViewById<TextView>(R.id.chatPersonName)
    val chatPersonStatus = itemView.findViewById<TextView>(R.id.chatPersonStatus)
    val userImage = itemView.findViewById<ImageView>(R.id.userImage)
}