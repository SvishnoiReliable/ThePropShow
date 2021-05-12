package com.ongraph.realestate.utils

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import com.google.gson.Gson
import com.ongraph.realestate.R
import com.ongraph.realestate.bean.response.GeneralResponse
import com.ongraph.realestate.callbacks.AppCallBackListner
import com.ongraph.realestate.rest.ApiClient
import com.ongraph.realestate.rest.ApiInterface
import com.ongraph.realestate.ui.activities.LoginActivity
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object GlobalVariable {
    private lateinit var progressDialog: ProgressDialog

    var LOC_REQ_CODE: Int = 111
    var PICK_LOCATION: Int = 2001

    var PICK_IMAGE_MULTIPLE: Int = 1221
    var PICK_XLS_FILE: Int = 1222

    var SMALL_FONT = 0.9f
    var MEDIUM_FONT = 1f
    var LARGE_FONT = 1.1f

    fun callLogoutApi(context: Context) {
        progressDialog = ProgressDialog(context)
        progressDialog.setMessage("Please wait")
        progressDialog.setCancelable(false)
        progressDialog.show()

        ApiClient.getClient()?.create(ApiInterface::class.java)?.logoutApi()
            ?.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>?, response: Response<ResponseBody>?
                ) {
                    progressDialog.dismiss()
                    try {
                        val mGson = Gson()
                        if (response?.body() != null && response.isSuccessful) {
                            val mResponse = mGson.fromJson(
                                response.body()!!.string(), GeneralResponse::class.java
                            )
                            SharedPrefsHelper.getInstance().clearPrefs()
                            DialogUtils.showAlertDialog(context, mResponse.getMessage()!!,
                                object : AppCallBackListner.DialogClickCallback {
                                    override fun onButtonClick() {
                                        val intent = Intent(context, LoginActivity::class.java)
                                        context.startActivity(intent)
                                        (context as Activity).finish()
                                    }
                                })
                        } else if (response?.errorBody() != null) {
                            val error = mGson.fromJson(
                                response.errorBody()!!.string(), GeneralResponse::class.java
                            )
                            if (error.getMessage() != null) {
                                DialogUtils.showAlertDialog(
                                    context, error.getMessage()!!,
                                    null
                                )
                            } else {
                                AppUtils.showToast(
                                    (context as Activity).findViewById(android.R.id.content),
                                    context.getString(R.string.somethingWrong)
                                )
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        AppUtils.showToast(
                            (context as Activity).findViewById(android.R.id.content),
                            context.getString(R.string.somethingWrong)
                        )
                    }
                }

                override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                    progressDialog.dismiss()
                    AppUtils.showToast(
                        (context as Activity).findViewById(android.R.id.content),
                        context.getString(R.string.somethingWrong)
                    )
                }
            })
    }
}