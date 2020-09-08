package com.example.whatsappclone.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.whatsappclone.R
import com.example.whatsappclone.UserViewHolder
import com.example.whatsappclone.models.User
import com.firebase.ui.firestore.paging.FirestorePagingAdapter
import kotlinx.android.synthetic.main.fragment_chats.*

class PeopleFragment : Fragment() {

    lateinit var madapter: FirestorePagingAdapter<User, UserViewHolder>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_chats,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.apply{
            layoutManager = LinearLayoutManager(requireContext())
            adapter = madapter
        }
    }
}
