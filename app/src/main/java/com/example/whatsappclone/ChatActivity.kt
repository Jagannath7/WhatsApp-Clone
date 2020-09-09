package com.example.whatsappclone

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.whatsappclone.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import com.vanniktech.emoji.EmojiManager
import com.vanniktech.emoji.google.GoogleEmojiProvider
import kotlinx.android.synthetic.main.activity_chat.*

const val UID = "uid"
const val NAME = "name"
const val IMAGE = "photo"

class ChatActivity : AppCompatActivity() {

    private val friendId by lazy{
        intent.getStringExtra(UID)
    }

    private val name by lazy {
        intent.getStringExtra(NAME)
    }

    private val image by lazy {
        intent.getStringExtra(IMAGE)
    }

    private val mCurrentUid by lazy{
        FirebaseAuth.getInstance().uid!!
    }

    private val db by lazy{
        FirebaseDatabase.getInstance()
    }

    lateinit var currentUser: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EmojiManager.install(GoogleEmojiProvider())
        setContentView(R.layout.activity_chat)

        nameTv.text = name
        Picasso.get().load(image).into(userImgView)

    }
}