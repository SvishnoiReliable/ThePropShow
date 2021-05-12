package com.ongraph.realestate.ui.fragments

import android.app.Activity
import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.google.gson.Gson
import com.ongraph.realestate.R
import com.ongraph.realestate.bean.response.Event
import com.ongraph.realestate.bean.response.GeneralResponse
import com.ongraph.realestate.bean.response.PostListResponse
import com.ongraph.realestate.rest.ApiClient
import com.ongraph.realestate.rest.ApiInterface
import com.ongraph.realestate.ui.adapter.EventAdapter
import com.ongraph.realestate.ui.adapter.EventFeatureAdapter
import com.ongraph.realestate.ui.adapter.EventViewPagerAdapter
import com.ongraph.realestate.utils.AppUtils
import com.ongraph.realestate.utils.DialogUtils
import kotlinx.android.synthetic.main.fragment_explore.view.*
import kotlinx.android.synthetic.main.fragment_subevent.view.recyclerview
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ExploreFragment : Fragment() {
    private lateinit var containerView: View
    private lateinit var adapter:EventAdapter

    private lateinit var adapterViewPager: EventFeatureAdapter
    var eventList: ArrayList<Event> = ArrayList()
    var featureEventList: ArrayList<Event> = ArrayList()
    var isLoading =false
    var page=1
    private lateinit var progressDialog: ProgressDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        containerView= inflater.inflate(R.layout.fragment_explore, container, false)
        initData()
        return containerView
    }

    private fun initData(){
        val linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        containerView.recyclerview.setLayoutManager(linearLayoutManager)
        adapter = EventAdapter(activity!!,eventList,true)
        containerView.recyclerview.setAdapter(adapter)
        containerView.recyclerview.visibility= View.VISIBLE

        progressDialog = ProgressDialog(context)
        progressDialog.setMessage("Please wait")
        progressDialog.setCancelable(false)

        if (arguments != null) {
            //isPast = arguments!!.getBoolean("index")
        }


        containerView.viewPager.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) { /*empty*/
            }

            override fun onPageSelected(position: Int) {
                containerView.pageIndicatorView.setSelection(position)
            }

            override fun onPageScrollStateChanged(state: Int) { /*empty*/
            }
        })


        callPostEventApi(true)

        if(page>0)
            callPostEventApi(false)

        containerView.recyclerview.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
                if (dy > 0) {
                    Log.d("Scroll",dy.toString())
                    val totalItemCount = linearLayoutManager.itemCount
                    val childCount = linearLayoutManager.childCount
                    val firstVisibleItemPositions: Int =
                        linearLayoutManager.findFirstVisibleItemPosition()

                    Log.d("childCount",childCount.toString())
                    Log.d("firstVisibleIts",firstVisibleItemPositions.toString())
                    Log.d("totalItemCount",totalItemCount.toString())

                    if (!isLoading && page>0) {
                        if (childCount + firstVisibleItemPositions >= totalItemCount) {
                            Log.d("Scrollok",dy.toString())
                            page += 1
                            //callPostEventApi(false)
                        }
                    }

                }
            }
        })
    }

    private fun callPostEventApi(isPast:Boolean) {
        isLoading=true

        progressDialog.show()

        ApiClient.getClient()?.create(ApiInterface::class.java)?.getExploreEvent(isPast,page,10)
            ?.enqueue(object : Callback<PostListResponse> {
                override fun onResponse(
                    call: Call<PostListResponse>?, response: Response<PostListResponse>?
                ) {

                    try {
                        progressDialog.dismiss()
                    }catch (e:java.lang.Exception){

                    }
                    try {
                        val mGson = Gson()
                        if (response?.body() != null && response.isSuccessful) {

                            if(isPast) {
                                eventList.addAll(response.body()!!.getData()!!.getEvents()!!)
                                adapter.notifyDataSetChanged()

                                if (response.body()!!.getData()!!.getTotal()!! > eventList.size) {
                                    page = -1
                                }
                            }else{
                                featureEventList.addAll(response.body()!!.getData()!!.getEvents()!!)
                                containerView.viewPager.setAdapter(
                                    EventFeatureAdapter(
                                        activity!!,
                                        featureEventList))


                                containerView.pageIndicatorView.count = featureEventList.size // specify total count of indicators
                                containerView.pageIndicatorView.selection = 0

                            }

                        } else if (response?.errorBody() != null) {
                            page=-1
                            val error = mGson.fromJson(
                                response.errorBody()!!.string(), GeneralResponse::class.java
                            )
                            if (error.getMessage() != null) {
                                DialogUtils.showAlertDialog(
                                    activity!!, error.getMessage()!!,
                                    null
                                )
                            } else {
                                AppUtils.showToast(
                                    (context as Activity).findViewById(android.R.id.content),
                                    getString(R.string.somethingWrong)
                                )
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        AppUtils.showToast(
                            (context as Activity).findViewById(android.R.id.content),
                            getString(R.string.somethingWrong)
                        )
                    }finally {

                        isLoading=false
                    }
                }

                override fun onFailure(call: Call<PostListResponse>?, t: Throwable?) {
                    progressDialog.dismiss()
                    isLoading=false
                    AppUtils.showToast(
                        (context as Activity).findViewById(android.R.id.content),
                        getString(R.string.somethingWrong)
                    )
                }
            })
    }
}
