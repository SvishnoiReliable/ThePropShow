package com.ongraph.realestate.ui.activities

import android.annotation.TargetApi
import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.WindowManager
import com.google.gson.Gson
import com.ongraph.realestate.R
import com.ongraph.realestate.bean.request.LoginRequest
import com.ongraph.realestate.bean.response.GeneralResponse
import com.ongraph.realestate.bean.response.ProfileResponse
import com.ongraph.realestate.callbacks.AppCallBackListner
import com.ongraph.realestate.rest.ApiClient
import com.ongraph.realestate.rest.ApiInterface
import com.ongraph.realestate.utils.AppUtils
import com.ongraph.realestate.utils.AppUtils.showToast
import com.ongraph.realestate.utils.DialogUtils
import com.ongraph.realestate.utils.SharedPrefsHelper
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.etEmail
import kotlinx.android.synthetic.main.dialog_forgot_password.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : BaseActivity() {

    private lateinit var progressDialog: ProgressDialog
    private lateinit var dialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initValues()
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    fun changeStatusBarColor(activity: Activity) {
        val background: Drawable = activity.resources.getDrawable(R.mipmap.bg)
        val window = activity.window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = activity.resources.getColor(android.R.color.transparent)
        //        window.setNavigationBarColor(activity.getResources().getColor(android.R.color.transparent));
        window.setBackgroundDrawable(background)
    }

    private fun initValues() {
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Please wait")
        progressDialog.setCancelable(false)

        changeStatusBarColor(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            AppUtils.spannableText(
                tvSingup,
                getString(R.string.sign_up_acc),
                "Sign Up",
                getColor(R.color.white)
            )
        }

        tvSingup.setOnClickListener {
            startActivity(Intent(this@LoginActivity, SignUpActivity::class.java))
        }
        tvForgot.setOnClickListener {
            openForgotPasswordDialog()
        }

        btnSignIn.setOnClickListener {
            AppUtils.hideKeyboard(this)
            if (validate()) {
                if (AppUtils.isConnected(this)) {
                    callLoginApi()
                } else {
                    showToast(
                        findViewById(android.R.id.content),
                        getString(R.string.chk_network)
                    )
                }
            }
        }
    }

    private fun openForgotPasswordDialog() {
        dialog = Dialog(this, R.style.FullScreenDialog)
        dialog.setContentView(R.layout.dialog_forgot_password)

        dialog.btnSend.setOnClickListener {
            AppUtils.hideDialogKeyboard(this, dialog)
            if (validateForgetPassword()) {
                if (AppUtils.isConnected(this)) {
                    callForgotPasswordApi(dialog.etEmail.text.toString().trim())
                } else {
                    showToast(
                        dialog.window!!.decorView.findViewById(android.R.id.content),
                        getString(R.string.chk_network)
                    )
                }
            }
        }
        dialog.show()
    }

    private fun validateForgetPassword(): Boolean {
        if (TextUtils.isEmpty(dialog.etEmail.text.toString().trim())) {
            dialog.etEmail.error = getString(R.string.valid_email)
            dialog.etEmail.requestFocus()
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(dialog.etEmail.text.toString().trim()).matches()) {
            dialog.etEmail.error = getString(R.string.valid_email)
            dialog.etEmail.requestFocus()
            return false
        }
        return true
    }

    private fun validate(): Boolean {
        if (TextUtils.isEmpty(etEmail.text.toString().trim())) {
            etEmail.error = getString(R.string.valid_email)
            etEmail.requestFocus()
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(etEmail.text.toString().trim()).matches()) {
            etEmail.error = getString(R.string.valid_email)
            etEmail.requestFocus()
            return false
        }
        if (TextUtils.isEmpty(etPassword.text.toString().trim())) {
            etPassword.error = getString(R.string.valid_password)
            etPassword.requestFocus()
            return false
        }
        if (etPassword.text.toString().trim().length < 6) {
            etPassword.error = getString(R.string.password_short)
            return false
        }
        return true
    }

    private fun callLoginApi() {
        progressDialog.show()

        try {
            val mLoginRequest = LoginRequest()
            mLoginRequest.email = etEmail.text.toString().trim()
            mLoginRequest.password = etPassword.text.toString().trim()

            ApiClient.getClient()?.create(ApiInterface::class.java)?.login(mLoginRequest)
                ?.enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(
                        call: Call<ResponseBody>?, response: Response<ResponseBody>
                    ) {
                        progressDialog.dismiss()
                        try {
                            val gson = Gson()
                            if (response.body() != null && response.isSuccessful) {
                                val mResponse = gson.fromJson(
                                    response.body()!!.string(), ProfileResponse::class.java
                                )

                                val header = response.headers().get("x-auth")
                                SharedPrefsHelper.getInstance().save("x-auth", header)

                                SharedPrefsHelper.getInstance().saveLoginData(mResponse.getData())
                                SharedPrefsHelper.getInstance()
                                    .save(
                                        "isProfileSetup",
                                        SharedPrefsHelper.getInstance().loginData.isProfileSetup
                                    )
                                SharedPrefsHelper.getInstance()
                                    .save(
                                        "isEmailVerified",
                                        SharedPrefsHelper.getInstance().loginData.isEmailVerified
                                    )

                                if(SharedPrefsHelper.getInstance().loginData.isProfileSetup!!){
                                    SharedPrefsHelper.getInstance().save("userID", SharedPrefsHelper.getInstance().loginData.firstName!!)
                                }


                                DialogUtils.showAlertDialog(
                                    this@LoginActivity,
                                    mResponse.getMessage()!!,
                                    object : AppCallBackListner.DialogClickCallback {
                                        override fun onButtonClick() {
                                            if (!SharedPrefsHelper.getInstance().loginData.isEmailVerified!!) {
                                                val mIntent = Intent(
                                                    this@LoginActivity,
                                                    OTPVerificationActivity::class.java
                                                )
                                                startActivity(mIntent)
                                                finish()
                                            } else if (!SharedPrefsHelper.getInstance().loginData.isProfileSetup!!) {
                                                val mIntent = Intent(
                                                    this@LoginActivity,
                                                    CompleteProfileActivity::class.java
                                                )
                                                startActivity(mIntent)
                                                finish()
                                            } else {
                                                val nIntent = Intent(
                                                    this@LoginActivity,
                                                    HomeActivity::class.java
                                                )
                                                startActivity(nIntent)
                                                finish()
                                            }
                                        }
                                    })
                            } else if (response.errorBody() != null) {
                                val error = gson.fromJson(
                                    response.errorBody()!!.string(), GeneralResponse::class.java
                                )
                                if (error.getMessage() != null) {
                                    DialogUtils.showAlertDialog(
                                        this@LoginActivity, error.getMessage()!!, null
                                    )
                                } else {
                                    showToast(
                                        findViewById(android.R.id.content),
                                        getString(R.string.somethingWrong)
                                    )
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            showToast(
                                findViewById(android.R.id.content),
                                getString(R.string.somethingWrong)
                            )
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                        progressDialog?.dismiss()
                        showToast(
                            findViewById(android.R.id.content),
                            getString(R.string.somethingWrong)
                        )
                    }
                })

        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun callForgotPasswordApi(email: String) {
        progressDialog?.show()
        val mLoginRequest = LoginRequest()
        mLoginRequest.email = email

        ApiClient.getClient()?.create(ApiInterface::class.java)
            ?.forgotPassword(mLoginRequest)?.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>?,
                    response: Response<ResponseBody>?
                ) {
                    progressDialog?.dismiss()
                    try {
                        val gson = Gson()
                        if (response?.body() != null && response.isSuccessful) {
                            val mResponse = gson.fromJson(
                                response.body()!!.string(), GeneralResponse::class.java
                            )
                            DialogUtils.showAlertDialog(
                                this@LoginActivity,
                                mResponse.getMessage()!!,
                                object : AppCallBackListner.DialogClickCallback {
                                    override fun onButtonClick() {
                                        dialog.dismiss()
                                    }
                                })
                        } else if (response?.errorBody() != null) {
                            val error = gson.fromJson(
                                response.errorBody()!!.string(), GeneralResponse::class.java
                            )
                            if (error.getMessage() != null) {
                                DialogUtils.showAlertDialog(
                                    this@LoginActivity, error.getMessage()!!, null
                                )
                            } else {
                                showToast(
                                    dialog.window!!.decorView.findViewById(android.R.id.content),
                                    getString(R.string.somethingWrong)
                                )
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        showToast(
                            dialog.window!!.decorView.findViewById(android.R.id.content),
                            getString(R.string.somethingWrong)
                        )
                    }
                }

                override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                    progressDialog?.dismiss()
                    showToast(
                        dialog.window!!.decorView.findViewById(android.R.id.content),
                        getString(R.string.somethingWrong)
                    )
                }
            })
    }


    private fun callLogoutApi() {
        progressDialog = ProgressDialog(this)
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
                            DialogUtils.showAlertDialog(this@LoginActivity,
                                mResponse.getMessage()!!,
                                object : AppCallBackListner.DialogClickCallback {
                                    override fun onButtonClick() {
                                        /*val intent = Intent(this@LoginActivity, LoginActivity::class.java)
                                        startActivity(intent)
                                        finish()*/
                                    }
                                })
                        } else if (response?.errorBody() != null) {
                            val error = mGson.fromJson(
                                response.errorBody()!!.string(), GeneralResponse::class.java
                            )
                            if (error.getMessage() != null) {
                                DialogUtils.showAlertDialog(
                                    this@LoginActivity, error.getMessage()!!,
                                    null
                                )
                            } else {
                                AppUtils.showToast(
                                    this@LoginActivity.findViewById(android.R.id.content),
                                    getString(R.string.somethingWrong)
                                )
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        AppUtils.showToast(
                            findViewById(android.R.id.content),
                            getString(R.string.somethingWrong)
                        )
                    }
                }

                override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                    progressDialog.dismiss()
                    AppUtils.showToast(
                        findViewById(android.R.id.content),
                        getString(R.string.somethingWrong)
                    )
                }
            })
    }
}

