package com.example.socketchat.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.socketchat.R
import com.example.socketchat.models.User
import kotlinx.android.synthetic.main.user_item.view.*

class UserAdapter(
        private var context: Context,
        private var data: ArrayList<User>,
        private val click: OnItemClickListener
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userImage = view.txtUserNameFirstChar!!
        val userName = view.txtUserName!!
        val userEmail = view.txtUserEmail!!
        val selected = view.imgSelectUser!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false)
        return UserViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.userName.text = data[position].userName
        holder.userEmail.text = data[position].userEmail
        holder.userImage.text = data[position].userName!![0].toString().toUpperCase()

        holder.itemView.setOnClickListener {
            click.onItemClick(position)
        }

        holder.itemView.setOnLongClickListener {
            if (holder.selected.visibility == View.GONE)
                holder.selected.visibility = View.VISIBLE
            else
                holder.selected.visibility = View.GONE
            click.onItemLongClick(position)
            true
        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
        fun onItemLongClick(position: Int)
    }

}
