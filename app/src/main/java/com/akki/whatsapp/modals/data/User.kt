package com.akki.whatsapp.modals.data

import com.google.firebase.firestore.FieldValue


data class User(val name:String,
                val avatarURL:String,
                val uid:String,
                val deviceToken:String,
                val status:String,
                val onlineStatus :String,
                ) {
    constructor() : this("","","","","", "" )
    constructor(name: String,avatarURL: String,uid: String,onlineStatus: String) :this(name,avatarURL,uid,onlineStatus,"Hey there I am using whatsapp!","")

}