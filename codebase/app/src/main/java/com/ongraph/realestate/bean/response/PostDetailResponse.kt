package com.ongraph.realestate.bean.response

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName

class PostDetailResponse {
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

        @SerializedName("event_id")
        @Expose
        private var event_id: String? = null


        @SerializedName("title")
        @Expose
        private var title: String? = null

        @SerializedName("description")
        @Expose
        private var description: String? = null

        @SerializedName("coverPhoto")
        @Expose
        private var coverPhoto: String? = null


        @SerializedName("coverPhotoThumb")
        @Expose
        private var coverPhotoThumb: String? = null

        @SerializedName("startTime")
        @Expose
        private var startTime: Long? = null

        @SerializedName("endTime")
        @Expose
        private var endTime: Long? = null


        @SerializedName("speakerList")
        @Expose
        private var speakerList: ArrayList<Speaker>? = null

        @SerializedName("agendaList")
        @Expose
        private var agendaList: ArrayList<Event>? = null


        @SerializedName("attendeeList")
        @Expose
        private var attendeeList: Attendee? = null


        fun getEvent_id(): String? {
            return event_id
        }

        fun setEvent_id(event_id: String?) {
            this.event_id = event_id
        }

        fun getdescription(): String? {
            return description
        }

        fun setdescription(description: String?) {
            this.description = description
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


        fun getAgendaListt(): ArrayList<Event>? {
            return agendaList
        }

        fun setAgendaList(speakerList: ArrayList<Event>?) {
            this.agendaList = agendaList
        }


        fun getAttendeeList(): Attendee? {
            return attendeeList
        }

        fun setAttendeeList(attendeeList: Attendee?) {
            this.attendeeList = attendeeList
        }

    }
}