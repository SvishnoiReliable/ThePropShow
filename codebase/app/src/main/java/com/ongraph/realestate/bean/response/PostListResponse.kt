package com.ongraph.realestate.bean.response

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName

class PostListResponse {
    @SerializedName("status")
    @Expose
    private var status: Int? = null
    @SerializedName("message")
    @Expose
    private var message: String? = null
    @SerializedName("data")
    @Expose
    private val data: Data? = null

    fun getStatus(): Int? {
        return status
    }

    fun setStatus(status: Int?) {
        this.status = status
    }

    fun getMessage(): String? {
        return message
    }

    fun setMessage(message: String?) {
        this.message = message
    }

    fun getData(): Data? {
        return data
    }

    public class Data {

        @SerializedName("docs")
        @Expose
        private var eventList: ArrayList<Event>? = null

        @SerializedName("total")
        @Expose
        private var total: Int? = null


        fun getTotal(): Int? {
            return total
        }

        fun setTotal(total: Int?) {
            this.total = total
        }


        fun getEvents(): ArrayList<Event>? {
            return eventList
        }

        fun setEvents(eventList: ArrayList<Event>?) {
            this.eventList = eventList
        }

    }
}