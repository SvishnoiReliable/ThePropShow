package com.ongraph.realestate.bean.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ProfileResponse {
    @SerializedName("status")
    @Expose
    private var status: Int? = null
    @SerializedName("message")
    @Expose
    private var message: String? = null
    @SerializedName("data")
    @Expose
    private var data: Data? = null

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

    fun setData(data: Data?) {
        this.data = data
    }

    class Data {
        @SerializedName("isEmailVerified")
        @Expose
        var isEmailVerified: Boolean? = null
        @SerializedName("isProfileSetup")
        @Expose
        var isProfileSetup: Boolean? = null
        @SerializedName("email")
        @Expose
        var email: String? = null
        @SerializedName("userType")
        @Expose
        var userType: Int? = null
        @SerializedName("description")
        @Expose
        var description: String? = null
        @SerializedName("firstName")
        @Expose
        var firstName: String? = null
        @SerializedName("lastName")
        @Expose
        var lastName: String? = null
        @SerializedName("profilePic")
        @Expose
        var profilePic: String? = null
        @SerializedName("profilePicThumb")
        @Expose
        var profilePicThumb: String? = null
        @SerializedName("role")
        @Expose
        var role: String? = null

    }
}