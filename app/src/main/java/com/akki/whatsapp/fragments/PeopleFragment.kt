package com.akki.whatsapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.akki.whatsapp.R
import com.akki.whatsapp.modals.UserViewHolder
import com.akki.whatsapp.modals.data.EmptyViewHolder
import com.akki.whatsapp.modals.data.User
import com.firebase.ui.firestore.paging.FirestorePagingAdapter
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.firebase.ui.firestore.paging.LoadingState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.list_adapter.*
import java.lang.Exception

private const val NORMAL_VIEW_TYPE =2
private const val DELETE_VIEW_TYPE = 1
class PeopleFragment: Fragment() {
 lateinit var mAdapter :FirestorePagingAdapter<User,RecyclerView.ViewHolder>
 val database by lazy {
     FirebaseFirestore.getInstance().collection("users").orderBy("name",Query.Direction.ASCENDING)
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
private fun setupAdapter(){
    val setup = PagedList.Config.Builder().setEnablePlaceholders(false).setPageSize(10).setPrefetchDistance(2).build()
    val config = FirestorePagingOptions.Builder<User>().setLifecycleOwner(viewLifecycleOwner).setQuery(database,setup,User::class.java).build()
    mAdapter = object:FirestorePagingAdapter<User,RecyclerView.ViewHolder>(config){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            if(viewType==NORMAL_VIEW_TYPE){
            val li = LayoutInflater.from(parent.context)
            val itemView = li.inflate(R.layout.card_layout,parent,false)
            val holder = UserViewHolder(itemView)
                return holder
           }
            else{
                val li = LayoutInflater.from(parent.context)
                val itemView = li.inflate(R.layout.delete_layout,parent,false)
                val holder = EmptyViewHolder(itemView)
                return holder
            }

        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, p2: User) {
        if(holder is UserViewHolder){
            holder.lastChatDate.isVisible=false
            holder.unreadMessages.isVisible=false

            holder.chatPersonName.setText(p2.name)
            holder.chatPersonStatus.setText(p2.status)
            Picasso.get().load(p2.avatarURL).placeholder(R.mipmap.ic_default_avatar).into(holder.userImage)
        }

        }

        override fun onLoadingStateChanged(state: LoadingState) {

            super.onLoadingStateChanged(state)
        }

        override fun onError(e: Exception) {
            super.onError(e)
        }


        override fun getItemViewType(position: Int): Int {
            val item = getItem(position)!!.toObject(User::class.java)
            if(item!!.uid==mAuth.uid){
                return DELETE_VIEW_TYPE
            }
            else{
                return NORMAL_VIEW_TYPE
            }

        }

    }
}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        listItem.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter
        }
        super.onViewCreated(view, savedInstanceState)
    }
}