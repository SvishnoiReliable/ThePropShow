package com.ongraph.realestate.ui.adapter

import android.content.Context
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.ongraph.realestate.R
import com.ongraph.realestate.bean.response.Event
import com.ongraph.realestate.utils.AppUtils


class EventFeatureAdapter : PagerAdapter {
    private var mediaList: List<Event>? = null
    private var inflater: LayoutInflater
    private var context: Context
    private var mAppUtils: AppUtils? = null
    private var isShoppingHome: Boolean? = null

    constructor(
        context: Context, mediaList: List<Event>) {
        this.context = context
        this.mediaList = mediaList
        inflater = LayoutInflater.from(this.context)
    }



    override fun getCount(): Int {
        return  mediaList!!.size
    }

    override fun instantiateItem(view: ViewGroup, position: Int): Any {
        val imageLayout: View =
            inflater.inflate(R.layout.item_explore_event, view, false)!!
        val tvDate =
            imageLayout.findViewById<TextView>(R.id.tvDate)

        val img_deal_banner =
            imageLayout.findViewById<ImageView>(R.id.ivHeader)


        Glide.with(context).load(mediaList!![position]!!.getCoverPhoto())
            .apply(
                RequestOptions().placeholder(R.drawable.ic_avatar).error(R.drawable.ic_avatar)
                    .dontAnimate().diskCacheStrategy(DiskCacheStrategy.ALL)
            ).into(img_deal_banner)

        mediaList!![position].getStartTime()?.let {
            tvDate.text=AppUtils.getDateTime(it)
            Log.d("Time1",AppUtils.getDateTime(it))
        }

        mediaList!![position].getEndTime()?.let {
            tvDate.append(" - "+AppUtils.getDateTime(it))
        }


        view.addView(imageLayout, 0)
        return imageLayout
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun restoreState(state: Parcelable?, loader: ClassLoader?) {
        super.restoreState(state, loader)
    }


    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        (container as ViewPager).removeView(`object` as View?)
    }


}
