package com.ongraph.realestate.bean.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Eventfdhdfh {
    @SerializedName("event_id")
    @Expose
    private String event_id = null;


    @SerializedName("title")
    @Expose
    private String title= null;

    @SerializedName("coverPhoto")
    @Expose
    private String coverPhoto = null;


    @SerializedName("coverPhotoThumb")
    @Expose
    private String coverPhotoThumb = null;

    @SerializedName("startTime")
    @Expose
    private Long startTime = null;

    @SerializedName("endTime")
    @Expose
    private Long endTime = null;


    @SerializedName("speakerList")
    @Expose
    private ArrayList<Speaker> speakerList = null;

    public String getEvent_id() {
        return event_id;
    }

    public void setEvent_id(String event_id) {
        this.event_id = event_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCoverPhoto() {
        return coverPhoto;
    }

    public void setCoverPhoto(String coverPhoto) {
        this.coverPhoto = coverPhoto;
    }

    public String getCoverPhotoThumb() {
        return coverPhotoThumb;
    }

    public void setCoverPhotoThumb(String coverPhotoThumb) {
        this.coverPhotoThumb = coverPhotoThumb;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public ArrayList<Speaker> getSpeakerList() {
        return speakerList;
    }

    public void setSpeakerList(ArrayList<Speaker> speakerList) {
        this.speakerList = speakerList;
    }
}
