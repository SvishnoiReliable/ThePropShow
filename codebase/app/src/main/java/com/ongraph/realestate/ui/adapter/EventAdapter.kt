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
import com.ongraph.realestate.ui.activities.EventDetailActivity
import com.ongraph.realestate.utils.AppUtils
import kotlinx.android.synthetic.main.fragment_subevent.view.*


class EventAdapter(var context:Context,var eventList: ArrayList<Event>,var isMyEvent:Boolean) : RecyclerView.Adapter<EventAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event, parent, false)
        )
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
    override fun getItemCount(): Int {
        return eventList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        Glide.with(context).load(eventList[position].getCoverPhoto())
            .apply(
                RequestOptions().placeholder(R.drawable.ic_avatar).error(R.drawable.ic_avatar)
                    .dontAnimate().diskCacheStrategy(DiskCacheStrategy.ALL)
            ).into(holder.image_view)

        eventList[position].getStartTime()?.let {
            holder.date.text=AppUtils.getDateTime(it)
            Log.d("Time1",AppUtils.getDateTime(it))
        }

        eventList[position].getEndTime()?.let {
            holder.date.append(" - "+AppUtils.getDateTime(it))
            Log.d("Time2",AppUtils.getDateTime(it))
        }


         eventList[position].getdescription()?.let {
            holder.tvEvent.text=(it)
        }


        val linearLayoutManager = LinearLayoutManager(context,
            LinearLayoutManager.HORIZONTAL, false)
        holder.speakerImageRecyclerview.setLayoutManager(linearLayoutManager)
        val adapter = SpeakerImageAdapter(context!!,eventList!![position].getSpeakerList()!!)
        holder.speakerImageRecyclerview.setAdapter(adapter)

        if(isMyEvent){
            holder.btFree.visibility=View.GONE
        }else{
            holder.btFree.visibility=View.VISIBLE
        }

        holder.mainLayout.setOnClickListener {
            val  intent = Intent(context,EventDetailActivity::class.java)
            intent.putExtra("eventID",eventList[position].getEvent_id())
            intent.putExtra("isMyEvent",isMyEvent)
            context.startActivity(intent)
        }
    }


    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        val mainLayout = itemView.findViewById(R.id.mainLayout) as ConstraintLayout
        val image_view = itemView.findViewById(R.id.ivHeader) as ImageView
        val btFree = itemView.findViewById(R.id.btFree) as Button
        val date = itemView.findViewById(R.id.tvDate) as TextView
        val tvEvent = itemView.findViewById(R.id.tvEvent) as TextView
        val tvEventSpeaker = itemView.findViewById(R.id.tvEventSpeaker) as TextView
        val speakerImageRecyclerview = itemView.findViewById(R.id.speakerImageRecyclerview) as RecyclerView


    }
}