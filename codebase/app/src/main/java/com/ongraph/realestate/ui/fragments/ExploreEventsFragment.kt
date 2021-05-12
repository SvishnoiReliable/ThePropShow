package com.ongraph.realestate.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.ongraph.realestate.R
import com.ongraph.realestate.ui.adapter.EventViewPagerAdapter
import kotlinx.android.synthetic.main.fragment_event.*
import kotlinx.android.synthetic.main.fragment_event.view.*

class ExploreEventsFragment : Fragment() {

    lateinit var convertView:View
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        convertView=inflater.inflate(R.layout.fragment_event, container, false)
        setupViewPager()
        return convertView
    }

    private fun setupViewPager() {
        val headerArrayList =
            ArrayList<String>()
        headerArrayList.add(resources.getString(R.string.past))
        headerArrayList.add(resources.getString(R.string.upcomming))

        convertView.tvheader.text=getString(R.string.explore_event)

        val fragmentArrayList =
            ArrayList<Fragment>()
        val fragment1 = ExploreSubEventFragment()
        val args = Bundle()
        args.putBoolean("index", true)
        fragment1.setArguments(args)

        val fragment2 = ExploreSubEventFragment()
        val args2 = Bundle()
        args2.putBoolean("index", false)
        fragment2.setArguments(args2)

        fragmentArrayList.add(fragment1)
        fragmentArrayList.add(fragment2)


        convertView.viewPager.setAdapter(
            EventViewPagerAdapter(
                activity,
                childFragmentManager,
                fragmentArrayList,
                headerArrayList
            )
        )

        convertView.tabLayout.setupWithViewPager(convertView.viewPager)
        convertView.viewPager.offscreenPageLimit=1
    }
}
