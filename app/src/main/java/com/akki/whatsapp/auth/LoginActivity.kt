package com.akki.whatsapp.auth

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.addTextChangedListener
import com.akki.whatsapp.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    private lateinit var Phone_Number:String
    private lateinit var Country_Code:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        longTextLogin.text = "Please enter your mobile number. You will get a SMS\nincluding a verification code."
        phoneNumber.addTextChangedListener {
            if(it?.length==10){
                loginNext.isClickable=true
                loginNext.setBackgroundColor(getColor(R.color.purple_700))
            }
            else{
                loginNext.isClickable=false
                loginNext.setBackgroundColor(getColor(android.R.color.darker_gray))
            }
        }
loginNext.setOnClickListener {
    checkNumber()
    val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputManager.hideSoftInputFromWindow(loginNext.windowToken,InputMethodManager.RESULT_UNCHANGED_SHOWN)
}
    }
    private fun checkNumber(){
    Country_Code = countryCode.selectedCountryCodeWithPlus
        Phone_Number = Country_Code+ phoneNumber.text.toString()

        notifyUser()
    }
    private fun notifyUser(){
        MaterialAlertDialogBuilder(this).setMessage("We will be verifying your phone number:$Phone_Number\n" +
                "Is this OK, or would you like to edit the number?").setPositiveButton("OK"){_,_->
            showOTPActivity()
        }.setNegativeButton("Edit"){dialog,which->
            dialog.dismiss()
        }.setCancelable(false).create().show()
    }
    private fun showOTPActivity(){
        val intent = Intent(this, OTPActivity::class.java)
        intent.putExtra("phoneNumber",Phone_Number)
        startActivity(intent)
        finish()
    }
}