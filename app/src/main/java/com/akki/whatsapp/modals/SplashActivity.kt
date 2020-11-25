package com.akki.whatsapp.modals

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.akki.whatsapp.auth.LoginActivity
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val auth = FirebaseAuth.getInstance()
       if(auth.currentUser==null){
           startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
           finish()
       }
        else{
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
           finish()
        }

        }
    }
