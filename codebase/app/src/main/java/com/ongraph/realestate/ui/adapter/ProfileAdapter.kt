package com.ongraph.realestate.ui.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.ongraph.realestate.R
import com.ongraph.realestate.bean.response.Event
import com.ongraph.realestate.bean.response.Speaker
import com.ongraph.realestate.ui.activities.EventDetailActivity
import com.ongraph.realestate.utils.AppUtils
import kotlinx.android.synthetic.main.fragment_subevent.view.*


class ProfileAdapter(var context:Context, var eventList: ArrayList<Speaker>) : RecyclerView.Adapter<ProfileAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_profile_layout, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return eventList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        Glide.with(context).load(eventList[position].profilePic)
            .apply(
                RequestOptions().placeholder(R.drawable.ic_avatar).error(R.drawable.ic_avatar)
                    .dontAnimate().diskCacheStrategy(DiskCacheStrategy.ALL)
            ).into(holder.image_view)

        eventList[position].firstName?.let {
            holder.date.text=it
            Log.d("Time1",it)
        }


         eventList[position].role?.let {
            holder.tvEvent.text=it
        }

    }


    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        val image_view = itemView.findViewById(R.id.profile_image) as ImageView

        val date = itemView.findViewById(R.id.tvDate) as TextView
        val tvEvent = itemView.findViewById(R.id.tvEvent) as TextView

    }
}