package com.akki.whatsapp.auth

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.widget.addTextChangedListener
import com.akki.whatsapp.modals.MainActivity
import com.akki.whatsapp.R
import com.akki.whatsapp.modals.data.User
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {
    val storage by lazy{
        FirebaseStorage.getInstance()
    }
    val database by lazy {
        FirebaseFirestore.getInstance()
    }
    val authUser by lazy {  FirebaseAuth.getInstance()}
    lateinit var downloadURL : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
            profilePicture.setOnClickListener {
            pickFromGalary()}

        signupName.addTextChangedListener {
            if(it?.length!=0){
                signupNext.isEnabled=true
                signupNext.setBackgroundColor(getColor(R.color.purple_700))
            }
            else{
                signupNext.isEnabled=false
                signupNext.setBackgroundColor(getColor(android.R.color.darker_gray))
            }

            }
        signupNext.setOnClickListener {
            val name = signupName.text.toString()
            Log.d("name","name")
            Log.d("name",downloadURL)
            val user = User(name,downloadURL,authUser.uid.toString(),"online")
            database.collection("users").document(authUser.uid!!).set(user).addOnSuccessListener {
                startActivity(Intent(this@SignUpActivity, MainActivity::class.java))
                finish()
            }.addOnFailureListener {
                signupNext.isEnabled=true
            }
        }
    }



    private fun pickFromGalary(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent,1000)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode== Activity.RESULT_OK&&requestCode==1000){
            profilePicture.setImageURI(data?.data)
            data?.data?.let { uploadImage(it) }
        }
    }
    private fun uploadImage(data:Uri){
        signupNext.isEnabled=false
        val path = storage.reference.child("uploads/"+authUser.uid.toString())
        val imageUpload = path.putFile(data)
        imageUpload.continueWithTask(Continuation<UploadTask.TaskSnapshot,Task<Uri>>{task ->
            if(!task.isSuccessful){
                throw task.exception!!
            }
         return@Continuation path.downloadUrl
        }).addOnCompleteListener {
            if(it.isSuccessful){
                downloadURL = it.result.toString()
                signupNext.isEnabled=true
            }
        }
}
}