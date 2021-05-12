package com.ongraph.realestate.callbacks

interface DateCallBacks {

    interface DateSelectedListener {
        fun onDateSet(mObject: String)
    }

    interface TimeSelectedListener {
        fun onTimeSet(mObject: String)
    }
}