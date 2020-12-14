package com.akki.whatsapp.fragments

import android.content.Intent
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
import com.akki.whatsapp.adapter.EmptyDataHolder
import com.akki.whatsapp.adapter.ModalDataHolder
import com.akki.whatsapp.data.User
import com.akki.whatsapp.modals.ChatActivity
import com.firebase.ui.firestore.paging.FirestorePagingAdapter
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.card_layout.*
import kotlinx.android.synthetic.main.list_adapter.*

const val ModalData = 1
const val DeleteData = 2
class PeopleFragment: Fragment() {
val query by lazy{
    FirebaseFirestore.getInstance().collection("users").orderBy("name",Query.Direction.ASCENDING)
}
    val mAuth by lazy{
        FirebaseAuth.getInstance()
    }
    lateinit var firestorePagingAdapter : FirestorePagingAdapter<User,RecyclerView.ViewHolder>
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setupAdapter()
        return inflater.inflate(R.layout.list_adapter,container,false)

    }
    private fun setupAdapter(){
       val config = PagedList.Config.Builder().setPageSize(10).setEnablePlaceholders(false).setPrefetchDistance(2).build()
        val options = FirestorePagingOptions.Builder<User>().setQuery(query,config,User::class.java).setLifecycleOwner(viewLifecycleOwner).build()
        firestorePagingAdapter = object: FirestorePagingAdapter<User,RecyclerView.ViewHolder>(options){
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
                val inflater = {layout:Int->LayoutInflater.from(parent.context).inflate(layout,parent,false)}
                return when(viewType){
                   ModalData->{
                       ModalDataHolder(inflater(R.layout.card_layout))
                   }
                    else->{
                        EmptyDataHolder(inflater(R.layout.delete_layout))
                    }
                }
            }

            override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int, p2: User) {
                when(val event=p0){
                    is ModalDataHolder->{
                        event.unreadMessages.isVisible=false
                        event.lastDate.isVisible=false
                        event.chatPersonName.text=p2.name
                        event.chatPersonStatus.text=p2.status
                        Picasso.get().load(p2.avatarURL).into(event.userPicture)
                        event.bind(p2){name: String, id: String, image: String ->
                            val intent = Intent(requireContext(),ChatActivity::class.java)
                            intent.putExtra("NAME",name)
                            intent.putExtra("UID",id)
                            intent.putExtra("AVATAR_URL",image)
                            startActivity(intent)
                        }
                    }
                    else->{

                    }
                }

            }

            override fun getItemViewType(position: Int): Int {
                val item = getItem(position)?.toObject(User::class.java)
                if(item!!.uid==mAuth.uid!!){
                    return DeleteData
                }
                return ModalData
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        listItem.layoutManager = LinearLayoutManager(requireContext())
        listItem.adapter = firestorePagingAdapter

        super.onViewCreated(view, savedInstanceState)
    }
}