package com.ongraph.realestate.ui.activities

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import com.google.gson.Gson
import com.ongraph.realestate.BuildConfig
import com.ongraph.realestate.R
import com.ongraph.realestate.bean.response.GeneralResponse
import com.ongraph.realestate.bean.response.PostDetailResponse
import com.ongraph.realestate.rest.ApiClient
import com.ongraph.realestate.rest.ApiInterface
import com.ongraph.realestate.ui.adapter.EventViewPagerAdapter
import com.ongraph.realestate.ui.fragments.AboutEventFragment
import com.ongraph.realestate.ui.fragments.AgendaEventFragment
import com.ongraph.realestate.ui.fragments.AttendeesEventFragment
import com.ongraph.realestate.ui.fragments.SpeakerEventFragment
import com.ongraph.realestate.utils.AppUtils
import com.ongraph.realestate.utils.DialogUtils
import kotlinx.android.synthetic.main.activity_event_detail.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class EventDetailActivity : BaseActivity() {
    private var progressDialog: ProgressDialog? = null

    private var postDetailResponse:PostDetailResponse?=null
    private var eventID:String?=null
    private var isMyEvent=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_detail)

        progressDialog = ProgressDialog(this)
        progressDialog!!.setMessage("Please wait")
        progressDialog!!.setCancelable(false)

        if (intent != null && intent.getStringExtra("eventID") != null) {
            eventID=intent.getStringExtra("eventID")
            callPostEventApi(eventID!!)

            isMyEvent = intent.getBooleanExtra("isMyEvent",false)
        }

        if(isMyEvent){
            bottomLayout.visibility=View.GONE
        }else
            bottomLayout.visibility=View.VISIBLE


        ivBack2.setOnClickListener {
            onBackPressed()
        }

        ivBack.setOnClickListener {
            onBackPressed()
        }
        share2.setOnClickListener {
            share()
        }

        share.setOnClickListener {
            share()
        }

        btrsvp.setOnClickListener {
            bookPostEventApi(eventID!!)
        }

    }

    private fun share() {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(
            Intent.EXTRA_TEXT,
            "Hey check out an Event "+tvheader.text+" \nmy app at: https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID)
        sendIntent.type = "text/plain"
        startActivity(sendIntent)
    }

    private fun setupViewPager() {
        Glide.with(this).load(postDetailResponse!!.getData()!!.getCoverPhoto())
            .apply(
                RequestOptions().placeholder(R.drawable.ic_avatar).error(R.drawable.ic_avatar)
                    .dontAnimate().diskCacheStrategy(DiskCacheStrategy.ALL)
            ).into(header)

        tvheader.text=  postDetailResponse!!.getData()!!.getTitle()
        tvheader2.text=  postDetailResponse!!.getData()!!.getTitle()
        postDetailResponse!!.getData()!!.getStartTime()?.let {
            tvDate.text=AppUtils.getDateTime(it)
            Log.d("Time1",AppUtils.getDateTime(it))
        }

        postDetailResponse!!.getData()!!.getEndTime()?.let {
            tvDate.append(" - "+AppUtils.getDateTime(it))
            Log.d("Time2",AppUtils.getDateTime(it))
        }

        appbar.addOnOffsetChangedListener(OnOffsetChangedListener { appBarLayout, verticalOffset ->
            if (Math.abs(verticalOffset) - appBarLayout.totalScrollRange == 0) {
                anim_toolbar.visibility=View.VISIBLE
            } else {
                anim_toolbar.visibility=View.GONE
            }
        })

        val headerArrayList =
            ArrayList<String>()
        headerArrayList.add(resources.getString(R.string.about))
        headerArrayList.add(resources.getString(R.string.agenda))
        headerArrayList.add(resources.getString(R.string.speakers))
        headerArrayList.add(resources.getString(R.string.attendees))

        val fragmentArrayList =
            ArrayList<Fragment>()

        val fragment1 = AboutEventFragment()
        val args = Bundle()
        args.putString("data", postDetailResponse!!.getData()!!.getdescription())
        fragment1.setArguments(args)

        val fragment2 = AgendaEventFragment()
        val args2 = Bundle()
        args2.putSerializable("data", postDetailResponse!!.getData()!!.getAgendaListt())
        args2.putBoolean("isMyEvent", isMyEvent)
        fragment2.setArguments(args2)

        val fragment3= SpeakerEventFragment()
        val args3 = Bundle()
        args3.putSerializable("data", postDetailResponse!!.getData()!!.getSpeakerList())
        args3.putBoolean("isMyEvent", isMyEvent)
        fragment3.setArguments(args3)

        val fragment4= AttendeesEventFragment()
        val args4 = Bundle()
        args4.putSerializable("data", postDetailResponse!!.getData()!!.getAttendeeList()!!.getagendaSpeakerList())
        args4.putBoolean("isMyEvent", isMyEvent)
        fragment4.setArguments(args4)


        fragmentArrayList.add(fragment1)
        fragmentArrayList.add(fragment2)
        fragmentArrayList.add(fragment3)
        fragmentArrayList.add(fragment4)


        viewPager.setAdapter(
            EventViewPagerAdapter(
                this,
                supportFragmentManager,
                fragmentArrayList,
                headerArrayList
            )
        )

        tabLayout.setupWithViewPager(viewPager)
        viewPager.offscreenPageLimit=1
    }

    private fun callPostEventApi(id:String) {
        progressDialog!!.show()
        ApiClient.getClient()?.create(ApiInterface::class.java)?.getEventDetails(id)
            ?.enqueue(object : Callback<PostDetailResponse> {
                override fun onResponse(
                    call: Call<PostDetailResponse>?, response: Response<PostDetailResponse>?
                ) {
                    try {
                        val mGson = Gson()
                        if (response?.body() != null && response.isSuccessful) {
                            postDetailResponse=response.body()
                            setupViewPager()
                        } else if (response?.errorBody() != null) {
                            val error = mGson.fromJson(
                                response.errorBody()!!.string(), GeneralResponse::class.java
                            )
                            if (error.getMessage() != null) {
                                DialogUtils.showAlertDialog(
                                   this@EventDetailActivity, error.getMessage()!!,
                                    null
                                )
                            } else {
                                AppUtils.showToast(findViewById(android.R.id.content),
                                    getString(R.string.somethingWrong)
                                )
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        AppUtils.showToast(
                           findViewById(android.R.id.content),
                            getString(R.string.somethingWrong)
                        )
                    }finally {
                        progressDialog!!.dismiss()
                    }
                }

                override fun onFailure(call: Call<PostDetailResponse>?, t: Throwable?) {
                    progressDialog!!.dismiss()

                    AppUtils.showToast(findViewById(android.R.id.content),
                        getString(R.string.somethingWrong)
                    )
                }
            })
    }


    private fun bookPostEventApi(id:String) {
        progressDialog!!.show()
        ApiClient.getClient()?.create(ApiInterface::class.java)?.getBookEvent(id)
            ?.enqueue(object : Callback<GeneralResponse> {
                override fun onResponse(
                    call: Call<GeneralResponse>?, response: Response<GeneralResponse>?
                ) {
                    try {
                        val mGson = Gson()
                        if (response?.body() != null && response.isSuccessful) {
                           DialogUtils.showAlertDialog(
                                this@EventDetailActivity, response!!.message()!!,
                                null)
                        } else if (response?.errorBody() != null) {
                            val error = mGson.fromJson(
                                response.errorBody()!!.string(), GeneralResponse::class.java
                            )
                            if (error.getMessage() != null) {
                                DialogUtils.showAlertDialog(
                                    this@EventDetailActivity, error.getMessage()!!,
                                    null
                                )
                            } else {
                                AppUtils.showToast(findViewById(android.R.id.content),
                                    getString(R.string.somethingWrong)
                                )
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        AppUtils.showToast(
                            findViewById(android.R.id.content),
                            getString(R.string.somethingWrong)
                        )
                    }finally {
                        progressDialog!!.dismiss()
                    }
                }

                override fun onFailure(call: Call<GeneralResponse>?, t: Throwable?) {
                    progressDialog!!.dismiss()

                    AppUtils.showToast(findViewById(android.R.id.content),
                        getString(R.string.somethingWrong)
                    )
                }
            })
    }
}
