package com.ongraph.realestate.bean.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Event : Serializable {

    @SerializedName("event_id")
    @Expose
    private var event_id: String? = null


    @SerializedName("title")
    @Expose
    private var title: String? = null

    @SerializedName("description")
    @Expose
    private var description: String? = null


    @SerializedName("coverPhotoThumb")
    @Expose
    private var coverPhoto: String? = null


    @SerializedName("coverPhoto")
    @Expose
    private var coverPhotoThumb: String? = null

    @SerializedName("startTime")
    @Expose
    private var startTime: Long? = null

    @SerializedName("endTime")
    @Expose
    private var endTime: Long? = null

    @SerializedName(value = "speakerList", alternate = ["speakers"])
    @Expose
    private var speakerList: ArrayList<Speaker>? = null


    fun getdescription(): String? {
        return description
    }

    fun setdescription(description: String?) {
        this.description = description
    }

    fun getEvent_id(): String? {
        return event_id
    }

    fun setEvent_id(event_id: String?) {
        this.event_id = event_id
    }

    fun getTitle(): String? {
        return title
    }

    fun setTitle(title: String?) {
        this.title = title
    }

    fun getCoverPhoto(): String? {
        return coverPhoto
    }

    fun setCoverPhoto(coverPhoto: String?) {
        this.coverPhoto = coverPhoto
    }

    fun getCoverPhotoThumb(): String? {
        return coverPhotoThumb
    }

    fun setCoverPhotoThumb(coverPhotoThumb: String?) {
        this.coverPhotoThumb = coverPhotoThumb
    }

    fun getStartTime(): Long? {
        return startTime
    }

    fun setStartTime(startTime: Long?) {
        this.startTime = startTime
    }

    fun getEndTime(): Long? {
        return endTime
    }

    fun setEndTime(endTime: Long?) {
        this.endTime = endTime
    }

    fun getSpeakerList(): ArrayList<Speaker>? {
        return speakerList
    }

    fun setSpeakerList(speakerList: ArrayList<Speaker>?) {
        this.speakerList = speakerList
    }

    override fun toString(): String {
        return "Event(event_id=$event_id, title=$title, coverPhoto=$coverPhoto, coverPhotoThumb=$coverPhotoThumb, startTime=$startTime, endTime=$endTime, speakerList=$speakerList)"
    }


}