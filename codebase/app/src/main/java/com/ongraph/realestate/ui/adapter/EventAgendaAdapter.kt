package com.ongraph.realestate.ui.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ongraph.realestate.R
import com.ongraph.realestate.bean.response.Event
import com.ongraph.realestate.chatModule.Constants
import com.ongraph.realestate.chatModule.activities.LiveActivity
import com.ongraph.realestate.utils.AppUtils


class EventAgendaAdapter(var context:Context, var eventList: ArrayList<Event>, var isMyEvent:Boolean) : RecyclerView.Adapter<EventAgendaAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event_agenda, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return eventList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        eventList[position].getStartTime()?.let {
            holder.date.text=AppUtils.getTime(it)
            Log.d("Time1",AppUtils.getTime(it))
        }

        eventList[position].getEndTime()?.let {
            holder.date.append(" - "+AppUtils.getTime(it))
            Log.d("Time2",AppUtils.getTime(it))
        }

         eventList[position].getStartTime()?.let {
             holder.tvEvent.text=AppUtils.getDateTime(it)
        }

        val linearLayoutManager = LinearLayoutManager(context,
            LinearLayoutManager.VERTICAL, false)
        holder.speakerImageRecyclerview.setLayoutManager(linearLayoutManager)
        val adapter = ProfileAdapter(context!!,eventList!![position].getSpeakerList()!!)
        holder.speakerImageRecyclerview.setAdapter(adapter)


        if(isMyEvent){
            holder.btcheckin.visibility=View.VISIBLE
        }else{
            holder.btcheckin.visibility=View.GONE
        }

     holder.btcheckin.setOnClickListener {
         val  intent = Intent(context,LiveActivity::class.java)
         intent.putExtra(Constants.KEY_CLIENT_ROLE, io.agora.rtc.Constants.CLIENT_ROLE_AUDIENCE)
         intent.putExtra(Constants.KEY_CHANEL, eventList[position].getEvent_id())
         context.startActivity(intent)
        }
    }


    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val mainLayout = itemView.findViewById(R.id.mainLayout) as ConstraintLayout
        val btcheckin = itemView.findViewById(R.id.btcheckin) as Button
        val date = itemView.findViewById(R.id.tvDate) as TextView
        val tvEvent = itemView.findViewById(R.id.tvEvent) as TextView
        val speakerImageRecyclerview = itemView.findViewById(R.id.speakerImageRecyclerview) as RecyclerView
    }
}