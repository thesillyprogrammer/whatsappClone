package com.akki.whatsapp.auth

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.akki.whatsapp.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import kotlinx.android.synthetic.main.activity_o_t_p.*
import java.util.concurrent.TimeUnit


class OTPActivity : AppCompatActivity() {
    lateinit var number:String
    lateinit var authenticationId:String
    lateinit var code: PhoneAuthProvider.ForceResendingToken
    lateinit var callback :PhoneAuthProvider.OnVerificationStateChangedCallbacks
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_o_t_p)
        number = intent.getStringExtra("phoneNumber")!!
        headingOtp.text = "Verify $number"
        countDown()
        SpannableText()
        Callbacks()
        SendToServer()
        OtpBox.addTextChangedListener {
            if(OtpBox.text.length!=6){
                otpVerify.isEnabled=false
                otpVerify.setBackgroundColor(getColor(android.R.color.darker_gray))
            }
            else{
                otpVerify.isEnabled=true
                otpVerify.setBackgroundColor(getColor(R.color.purple_700))
            }
        }
        otpVerify.setOnClickListener {
            val otp= OtpBox.text.toString()
            val credential = PhoneAuthProvider.getCredential(authenticationId,otp)
            signInWithPhoneCredentials(credential)
        }
        otpResend.setOnClickListener {
            val options = PhoneAuthOptions.newBuilder().setTimeout(60,TimeUnit.SECONDS).setCallbacks(callback).setPhoneNumber(number).setActivity(this).build()
            PhoneAuthProvider.verifyPhoneNumber(options)
        }
    }
    private fun countDown(){
         object:CountDownTimer(60000,1000){
            override fun onTick(millisUntilFinished: Long) {
                countDownTimer.text = getString(R.string.resendCode,(millisUntilFinished/1000).toString())
                countDownTimer.isVisible = true
                otpResend.isEnabled=false
                otpResend.setBackgroundColor(getColor(android.R.color.darker_gray))
            }

            override fun onFinish() {
                countDownTimer.isVisible=false
                otpResend.isEnabled=true
                otpResend.setBackgroundColor(getColor(R.color.purple_700))
            }

        }.start()
    }
    private fun SpannableText(){
        val spanString = SpannableString(getString(R.string.waitingString,number))
        val spanner = object: ClickableSpan(){
            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = ds.linkColor
                ds.isUnderlineText=false
            }

            override fun onClick(widget: View) {
                startActivity(Intent(this@OTPActivity, LoginActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK))
                finish()
            }
        }
        spanString.setSpan(spanner,spanString.length-13,spanString.length,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableText.movementMethod = LinkMovementMethod.getInstance()
        spannableText.text = spanString
    }
    private fun Callbacks(){
        callback=object:PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
            override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                val smsCode = p0.smsCode
                if(smsCode!=null){
                OtpBox.setText(smsCode)}
                signInWithPhoneCredentials(p0)
            }

            override fun onVerificationFailed(p0: FirebaseException) {
                if(p0 is FirebaseAuthInvalidCredentialsException){
                    val message = "Wrong Credentials Resend OTP"
                    materialDialogBuilder(message)
                }
            }

            override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                super.onCodeSent(p0, p1)
                authenticationId=p0
                code = p1
            }

        }
    }
    private fun SendToServer(){
        val options = PhoneAuthOptions.newBuilder().setActivity(this).setPhoneNumber(number).setTimeout(60,TimeUnit.SECONDS).setCallbacks(callback).build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }
    private fun signInWithPhoneCredentials(credentials:PhoneAuthCredential){
        val mAuth = FirebaseAuth.getInstance()
        mAuth.signInWithCredential(credentials).addOnSuccessListener {
            startActivity(Intent(this@OTPActivity, SignUpActivity::class.java))
            finish()
        }.addOnFailureListener {
            Toast.makeText(this,"Task Failed Successfully,Regenerating OTP",Toast.LENGTH_SHORT).show()
            startActivity(Intent(this@OTPActivity, LoginActivity::class.java))
        }
    }
    private fun materialDialogBuilder(message:String){
        MaterialAlertDialogBuilder(this).setMessage(message).setPositiveButton("OK"){_,_->
            startActivity(Intent(this@OTPActivity, LoginActivity::class.java))
        }.setNegativeButton("Cancel"){dialog,_->
            dialog.dismiss()
        }
    }
    private fun Permissions(){
        if((checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_DENIED)&&
                (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_DENIED)){
           requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),1001)
            requestPermissions(arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),1002)

        }
    }
}