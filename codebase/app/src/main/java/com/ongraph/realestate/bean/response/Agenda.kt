package com.ongraph.realestate.bean.response

data class Agenda(val title:String,
                   val format:String,
                   val description:String,
                   val startTime:Long,
                   val endTime:Long,
                   val speakers:ArrayList<Speaker>?) {
}


