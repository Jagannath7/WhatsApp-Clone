package com.example.whatsappclone

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.whatsappclone.models.User
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.list_item.view.*


class UserViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    fun bind(user: User) = with(itemView){

        countTv.isInvisible()
        timeTv.isInvisible()

        titleTv.text = user.name
        subTitleTv.text = user.status

        Picasso.get()
            .load(user.thumbImg)
            .placeholder(R.drawable.defaultavatar)
            .error(R.drawable.defaultavatar)
            .into(userImgView)
    }

    fun View.isInvisible(): Boolean {
        return this.visibility == View.INVISIBLE
    }
}