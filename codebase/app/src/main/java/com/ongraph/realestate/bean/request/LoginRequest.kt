package com.ongraph.realestate.bean.request

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName

class LoginRequest {
    @SerializedName("email")
    @Expose
    var email: String? = null
    @SerializedName("password")
    @Expose
    var password: String? = null
    @SerializedName("userType")
    @Expose
    var userType: String? = null
    @SerializedName("otp")
    @Expose
    var otp: Int? = null

}