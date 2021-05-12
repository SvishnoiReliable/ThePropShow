package com.ongraph.realestate.ui.adapter;

import android.content.Context;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.ongraph.realestate.R;

import java.util.ArrayList;

public class EventViewPagerAdapter extends FragmentStatePagerAdapter {
    ArrayList<Fragment> list = new ArrayList<>();  ArrayList<String> headerlist = new ArrayList<>();
    Context context;

    public EventViewPagerAdapter(Context context, FragmentManager fm, ArrayList<Fragment> list, ArrayList<String> headerlist) {
        super(fm);
        this.list=list;
        this.headerlist=headerlist;
        this.context=context;
    }

    @Override
    public Fragment getItem(int position) {
        return list.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return headerlist.get(position);
    }

    @Override
    public int getCount() {
        return list.size();
    }




}