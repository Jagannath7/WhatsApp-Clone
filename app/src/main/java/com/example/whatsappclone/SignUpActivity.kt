package com.example.whatsappclone

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
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

    val auth by lazy{
        FirebaseAuth.getInstance()
    }

    val database by lazy{
        FirebaseFirestore.getInstance()
    }

    private lateinit var downloadUrl: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        userImgView.setOnClickListener {
            checkForImagePermission()
        }

        nextBtn.setOnClickListener {
            nextBtn.isEnabled = false
            val name = nameEt.text.toString()
            if(name.isEmpty()){
                Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_LONG).show()
            }else if(!::downloadUrl.isInitialized){
                Toast.makeText(this, "Image cannot be empty", Toast.LENGTH_LONG).show()
            }else{
                val user = User(name,downloadUrl,downloadUrl,auth.uid!!)
                database.collection("users").document(auth.uid!!).set(user).addOnSuccessListener {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }.addOnFailureListener {
                    nextBtn.isEnabled = true
                }
            }
        }
    }

    private fun checkForImagePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
           if((checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) &&
               (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
           ){
                val permissionRead = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                val permisssionWrite = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

                requestPermissions(
                    permissionRead,
                    1001
                )

                requestPermissions(
                    permisssionWrite,
                    1002
                )

           }else{
               pickImageFromGallery()
           }
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(
            intent,
            1000
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == 1000){
            data?.data?.let {
                userImgView.setImageURI(it)
                uploadImage(it)
            }
        }
    }

    private fun uploadImage(it: Uri) {
        nextBtn.isEnabled = false
        val ref = storage.reference.child("uploads/" + auth.uid.toString())
        val uploadTask = ref.putFile(it)
        uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>>{ task ->
            if(!task.isSuccessful){
                task.exception?.let{
                    throw  it
                }
            }
            return@Continuation ref.downloadUrl
        }).addOnCompleteListener {task ->
            nextBtn.isEnabled = true
            if(task.isSuccessful){
                downloadUrl = task.result.toString()
            }else{

            }
        }.addOnFailureListener {

        }
    }
}