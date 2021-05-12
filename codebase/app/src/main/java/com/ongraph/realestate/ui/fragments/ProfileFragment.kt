package com.ongraph.realestate.ui.fragments

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import com.ongraph.realestate.R
import com.ongraph.realestate.bean.response.GeneralResponse
import com.ongraph.realestate.callbacks.AppCallBackListner
import com.ongraph.realestate.rest.ApiClient
import com.ongraph.realestate.rest.ApiInterface
import com.ongraph.realestate.ui.activities.EditProfileActivity
import com.ongraph.realestate.ui.activities.LoginActivity
import com.ongraph.realestate.utils.AppUtils
import com.ongraph.realestate.utils.DialogUtils
import com.ongraph.realestate.utils.SharedPrefsHelper
import kotlinx.android.synthetic.main.fragment_profile.view.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileFragment : Fragment() {

    private lateinit var containerView: View
    private lateinit var progressDialog: ProgressDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        containerView = inflater.inflate(R.layout.fragment_profile, container, false)
        initValues()
        return containerView
    }

    private fun initValues() {
        containerView.logout.setOnClickListener {
            AppUtils.logoutDialog((context as Activity),
                (context as Activity).getString(R.string.app_name),
                "Do you wish to logout?",
                object : AppCallBackListner {
                    override fun isSuccess(success: Boolean) {
                        if (success) {
                            if (AppUtils.isConnected(context as Activity)) {
                                callLogoutApi()
                            } else {
                                AppUtils.showToast(
                                    (context as Activity).findViewById(android.R.id.content),
                                    getString(R.string.chk_network)
                                )
                            }
                        }
                    }
                })
        }

        containerView.btnEdit.setOnClickListener {
            startActivity(Intent(activity, EditProfileActivity::class.java))
        }
        containerView.ivProfile.setOnClickListener {
            startActivity(Intent(activity, EditProfileActivity::class.java))
        }
    }

    private fun callLogoutApi() {
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
                            SharedPrefsHelper.getInstance()
                                .save(
                                    "isProfileSetup",
                                    SharedPrefsHelper.getInstance().loginData.isProfileSetup
                                )

                            if(SharedPrefsHelper.getInstance().loginData.isProfileSetup!!){
                                SharedPrefsHelper.getInstance().save("userID", SharedPrefsHelper.getInstance().loginData.firstName!!)
                            }

                            DialogUtils.showAlertDialog(activity!!, mResponse.getMessage()!!,
                                object : AppCallBackListner.DialogClickCallback {
                                    override fun onButtonClick() {
                                        val intent = Intent(activity, LoginActivity::class.java)
                                        startActivity(intent)
                                        (context as Activity).finish()
                                    }
                                })
                        } else if (response?.errorBody() != null) {
                            val error = mGson.fromJson(
                                response.errorBody()!!.string(), GeneralResponse::class.java
                            )
                            if (error.getMessage() != null) {
                                DialogUtils.showAlertDialog(
                                    activity!!, error.getMessage()!!,
                                    null
                                )
                            } else {
                                AppUtils.showToast(
                                    (context as Activity).findViewById(android.R.id.content),
                                    getString(R.string.somethingWrong)
                                )
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        AppUtils.showToast(
                            (context as Activity).findViewById(android.R.id.content),
                            getString(R.string.somethingWrong)
                        )
                    }
                }

                override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                    progressDialog.dismiss()
                    AppUtils.showToast(
                        (context as Activity).findViewById(android.R.id.content),
                        getString(R.string.somethingWrong)
                    )
                }
            })
    }

    override fun onResume() {
        super.onResume()
        setData()
    }

    private fun setData() {
        val mResponse = SharedPrefsHelper.getInstance().loginData
        if (mResponse != null) {
            containerView.etFName.setText(mResponse.firstName)
            containerView.etLName.setText(mResponse.lastName)
            containerView.etRole.setText(mResponse.role)
            containerView.etDesc.setText(mResponse.description)

            Glide.with(this).load(mResponse.profilePic).apply(
                RequestOptions().placeholder(R.mipmap.default_icon).error(R.mipmap.default_icon).diskCacheStrategy(
                    DiskCacheStrategy.ALL
                )
            ).into(containerView.ivProfile)
        }
    }
}
