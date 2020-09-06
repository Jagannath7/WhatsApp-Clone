package com.example.whatsappclone

import java.util.*

data class User(
    val name: String,
    val imageUrl: String,
    val thumbImg: String,
    val uid: String,
    val deviceToken: String,
    val status: String,
    val onlineStatus: Boolean,

) {

    constructor() : this("", "", "", "", "", "", "")

    constructor(name: String, imageUrl: String, thumbImg: String, uid: String) : this(
        name,
        imageUrl,
        thumbImg,
        uid,
        "",
        "Hey there! I am using Whatsapp",
        false
    )


}