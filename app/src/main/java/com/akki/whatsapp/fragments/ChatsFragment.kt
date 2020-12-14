package com.akki.whatsapp.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.akki.whatsapp.R
import com.akki.whatsapp.adapter.ChatHolder
import com.akki.whatsapp.data.Inbox
import com.akki.whatsapp.data.User
import com.akki.whatsapp.modals.ChatActivity
import com.akki.whatsapp.utils.formatAsListItem
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import com.vanniktech.emoji.EmojiManager
import com.vanniktech.emoji.EmojiPagerAdapter
import com.vanniktech.emoji.google.GoogleEmojiProvider
import kotlinx.android.synthetic.main.list_adapter.*

class ChatsFragment:Fragment() {
    lateinit var mAdapter: FirebaseRecyclerAdapter<Inbox,ChatHolder>
    val database by lazy {
        FirebaseDatabase.getInstance()
    }
    val mAuth by lazy {
        FirebaseAuth.getInstance()
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setupAdapter()
        return inflater.inflate(R.layout.list_adapter,container,false)
        
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        listItem.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        listItem.adapter = mAdapter
        super.onViewCreated(view, savedInstanceState)
    }

    private fun setupAdapter(){
        val query = database.reference.child("chats").child(mAuth.uid!!)
        val options = FirebaseRecyclerOptions.Builder<Inbox>().setLifecycleOwner(this).setQuery(query,Inbox::class.java).build()
        mAdapter = object:FirebaseRecyclerAdapter<Inbox,ChatHolder>(options){
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatHolder {
                val itemView = LayoutInflater.from(parent.context).inflate(R.layout.card_layout,parent,false)
                return ChatHolder(itemView)
            }

            override fun onBindViewHolder(holder: ChatHolder, position: Int, model: Inbox) {
                holder.userName.text = model.chatName
                holder.unreadMessages.isVisible= model.count>0
                holder.unreadMessages.text = model.count.toString()
                holder.lastChatDate.text = model.time.formatAsListItem(requireContext())
                holder.lastMessage.text = model.lastMessage
                Picasso.get().load(model.avatar_Url).placeholder(R.mipmap.ic_default_avatar).into(holder.userImage)
                holder.bind(model){ name:String, avatarURL:String->
                    val intent = Intent(requireContext(),ChatActivity::class.java)
                    intent.putExtra("Name",name)
                    intent.putExtra("Avatar",avatarURL)
                    intent.putExtra("uid",model.inboxUID)
                    database.getReference().child("chats").child(mAuth.uid!!).child(model.inboxUID).child("count").setValue(0)
                    startActivity(intent)
                }
            }
        }
    }
}