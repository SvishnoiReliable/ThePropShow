package com.ongraph.realestate.ui.fragments

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.google.gson.Gson
import com.ongraph.realestate.R
import com.ongraph.realestate.bean.response.Event
import com.ongraph.realestate.bean.response.GeneralResponse
import com.ongraph.realestate.bean.response.PostListResponse
import com.ongraph.realestate.bean.response.Speaker
import com.ongraph.realestate.callbacks.AppCallBackListner
import com.ongraph.realestate.rest.ApiClient
import com.ongraph.realestate.rest.ApiInterface
import com.ongraph.realestate.ui.activities.LoginActivity
import com.ongraph.realestate.ui.adapter.EventAdapter
import com.ongraph.realestate.ui.adapter.SpeakerProfileAdapter
import com.ongraph.realestate.utils.AppUtils
import com.ongraph.realestate.utils.DialogUtils
import com.ongraph.realestate.utils.SharedPrefsHelper
import kotlinx.android.synthetic.main.fragment_attend.view.*
import kotlinx.android.synthetic.main.item_event_agenda.view.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SpeakerEventFragment : Fragment() {
    private lateinit var containerView: View
    private lateinit var progressDialog: ProgressDialog
    var eventList: ArrayList<Speaker> = ArrayList()
    private lateinit var adapter:SpeakerProfileAdapter
    var isPast=false
    var isLoading =false
    var page=1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        containerView= inflater.inflate(R.layout.fragment_attend, container, false)
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
            eventList = arguments!!.getSerializable("data") as ArrayList<Speaker>
        }

        if(eventList!=null&&eventList.size>0){
            Log.d("specker list",eventList.size.toString()+" "+ eventList.toString())
            adapter = SpeakerProfileAdapter(activity!!,eventList)
            containerView.recyclerview.setAdapter(adapter)
            containerView.recyclerview.visibility= View.VISIBLE
            containerView.tvNodata1.visibility=View.GONE
        }else{
            containerView.tvNodata1.visibility=View.VISIBLE
        }

    }

}
