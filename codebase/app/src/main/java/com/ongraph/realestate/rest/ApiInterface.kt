package com.ongraph.realestate.rest

import com.ongraph.realestate.bean.request.LoginRequest
import com.ongraph.realestate.bean.response.GeneralResponse
import com.ongraph.realestate.bean.response.PostDetailResponse
import com.ongraph.realestate.bean.response.PostListResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface ApiInterface {

    @POST("user/signup/")
    @Headers("Content-Type: application/json;charset=UTF-8")
    fun signUp(@Body body: LoginRequest): Call<ResponseBody>

    @POST("user/login/")
    @Headers("Content-Type: application/json;charset=UTF-8")
    fun login(@Body body: LoginRequest): Call<ResponseBody>

    @POST("user/forgetpass/")
    @Headers("Content-Type: application/json;charset=UTF-8")
    fun forgotPassword(@Body body: LoginRequest): Call<ResponseBody>

    @PATCH("user/email_confirmation/")
    @Headers("Content-Type: application/json;charset=UTF-8")
    fun verifyEmail(@Body body: LoginRequest): Call<ResponseBody>

    @GET("user/email_confirmation")
    @Headers("Content-Type: application/json;charset=UTF-8")
    fun resendEmail(): Call<ResponseBody>

    @GET("user/profile")
    @Headers("Content-Type: application/json;charset=UTF-8")
    fun getProfile(): Call<ResponseBody>

    @DELETE("user/logout/")
    @Headers("Content-Type: application/json;charset=UTF-8")
    fun logoutApi(): Call<ResponseBody>

    @Multipart
    @PATCH("user/profile/")
    fun editProfile(@PartMap map: HashMap<String, RequestBody>, @Part file: MultipartBody.Part): Call<ResponseBody>

    @Multipart
    @PATCH("user/profile/")
    fun editProfile(@PartMap map: HashMap<String, RequestBody>): Call<ResponseBody>

    ///////*****************************************************///////////

    /*Events*/
    @GET("event/my/")
    @Headers("Content-Type: application/json;charset=UTF-8")
    fun getMyPastEvent(@Query("isPast") isPast: Boolean,
                     @Query("page") page: Int,
                     @Query("limit") limit: Int): Call<PostListResponse>

    @GET("event/all/")
    @Headers("Content-Type: application/json;charset=UTF-8")
    fun getExploreEvent(@Query("isPast") isPast: Boolean,
                     @Query("page") page: Int,
                     @Query("limit") limit: Int): Call<PostListResponse>

    @GET("event/attendees/")
    @Headers("Content-Type: application/json;charset=UTF-8")
    fun getAttendees(@Query("id") id: String,
                     @Query("page") page: Int,
                     @Query("limit") limit: Int): Call<PostListResponse>

    @GET("event")
    @Headers("Content-Type: application/json;charset=UTF-8")
    fun getEventDetails(@Query("id") id: String): Call<PostDetailResponse>

    @POST("event/book")
    @Headers("Content-Type: application/json;charset=UTF-8")
    fun getBookEvent(@Query("id") id: String): Call<GeneralResponse>
}