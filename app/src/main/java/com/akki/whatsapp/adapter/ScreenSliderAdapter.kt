package com.akki.whatsapp.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.akki.whatsapp.fragments.ChatsFragment
import com.akki.whatsapp.fragments.PeopleFragment


class ScreenSliderAdapter(fa: FragmentActivity): FragmentStateAdapter(fa) {

    override fun getItemCount(): Int {
       return 2
    }

    override fun createFragment(position: Int): Fragment {
      return when(position){
           0->ChatsFragment()
           else-> PeopleFragment()
       }
    }

}