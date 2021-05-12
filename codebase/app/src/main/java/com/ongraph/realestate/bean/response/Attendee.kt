package com.ongraph.realestate.bean.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Attendee :Serializable{
    @SerializedName("docs")
    @Expose
    private var agendaSpeakerList: ArrayList<Speaker>? = null

    @SerializedName("total")
    @Expose
    private var total: Int? = null


    fun getTotal(): Int? {
        return total
    }

    fun setTotal(total: Int?) {
        this.total = total
    }


    fun getagendaSpeakerList(): ArrayList<Speaker>? {
        return agendaSpeakerList
    }

    fun setagendaSpeakerList(agendaSpeakerList: ArrayList<Speaker>?) {
        this.agendaSpeakerList = agendaSpeakerList
    }

}


