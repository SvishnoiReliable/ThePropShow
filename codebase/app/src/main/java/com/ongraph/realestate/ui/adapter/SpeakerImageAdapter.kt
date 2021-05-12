package com.ongraph.realestate.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.ongraph.realestate.R
import com.ongraph.realestate.bean.response.Event
import com.ongraph.realestate.bean.response.Speaker


class SpeakerImageAdapter(var context:Context,var mList: ArrayList<Speaker>) : RecyclerView.Adapter<SpeakerImageAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_circel, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val marginParams = holder.image_view.layoutParams as ViewGroup.MarginLayoutParams
        marginParams.setMargins(0, 0, 0, 0)
            Glide.with(context).load(mList[position].profilePic)
                .apply(
                    RequestOptions().placeholder(R.drawable.ic_avatar).error(R.drawable.ic_avatar)
                        .dontAnimate().diskCacheStrategy(DiskCacheStrategy.ALL)
                ).into(holder.image_view)

    }


    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        val image_view = itemView.findViewById(R.id.profile_image) as ImageView



    }
}