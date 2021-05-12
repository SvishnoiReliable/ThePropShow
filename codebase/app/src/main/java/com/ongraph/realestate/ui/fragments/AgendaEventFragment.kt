package com.ongraph.realestate.ui.fragments

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.ongraph.realestate.R
import com.ongraph.realestate.bean.response.Event
import com.ongraph.realestate.ui.adapter.EventAgendaAdapter
import kotlinx.android.synthetic.main.fragment_agenda.view.*

class AgendaEventFragment : Fragment() {
    private lateinit var containerView: View
    private lateinit var progressDialog: ProgressDialog
    var eventList: ArrayList<Event> = ArrayList()
    private lateinit var adapter:EventAgendaAdapter
    var isMyEvent=false
    var isLoading =false
    var page=1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        containerView= inflater.inflate(R.layout.fragment_agenda, container, false)
        initData()
        return containerView
    }

    private fun initData(){
        progressDialog = ProgressDialog(context)
        progressDialog.setMessage("Please wait")
        progressDialog.setCancelable(false)

        val linearLayoutManager = LinearLayoutManager(activity,LinearLayoutManager.VERTICAL, false)
        containerView.recyclerview.setLayoutManager(linearLayoutManager)

        if (arguments != null) {
            eventList = arguments!!.getSerializable("data") as ArrayList<Event>

            isMyEvent = arguments!!.getBoolean("isMyEvent")
        }

        if(eventList!=null&&eventList.size>0){
            Log.d("agenda list",eventList.size.toString()+" "+ eventList.toString())
            adapter = EventAgendaAdapter(activity!!,eventList,isMyEvent)
            containerView.recyclerview.setAdapter(adapter)
            containerView.recyclerview.visibility= View.VISIBLE
            containerView.tvNodata1.visibility=View.GONE
        }else{
            containerView.tvNodata1.visibility=View.VISIBLE
        }
    }

}
