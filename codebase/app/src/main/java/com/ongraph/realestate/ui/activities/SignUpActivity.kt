package com.ongraph.realestate.ui.activities

import android.app.ProgressDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import com.google.gson.Gson
import com.ongraph.realestate.R
import com.ongraph.realestate.bean.request.LoginRequest
import com.ongraph.realestate.bean.response.GeneralResponse
import com.ongraph.realestate.callbacks.AppCallBackListner
import com.ongraph.realestate.rest.ApiClient
import com.ongraph.realestate.rest.ApiInterface
import com.ongraph.realestate.utils.AppUtils
import com.ongraph.realestate.utils.DialogUtils
import com.ongraph.realestate.utils.SharedPrefsHelper
import kotlinx.android.synthetic.main.activity_sign_up.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpActivity : BaseActivity() {
    private var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        initValues()
    }

    private fun initValues() {
        progressDialog = ProgressDialog(this)
        progressDialog!!.setMessage("Please wait")
        progressDialog!!.setCancelable(false)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            AppUtils.spannableText(
                tvSignin,
                getString(R.string.sign_in_acc),
                "Sign In",
                getColor(R.color.white)
            )
        }

        tvSignin.setOnClickListener {
            startActivity(Intent(this@SignUpActivity, LoginActivity::class.java))
        }

        btnSignUp.setOnClickListener {
            AppUtils.hideKeyboard(this)
            if (validate()) {
                if (AppUtils.isConnected(this)) {
                    callSignUpApi()
                } else {
                    AppUtils.showToast(
                        findViewById(android.R.id.content),
                        getString(R.string.chk_network)
                    )
                }
            }
        }
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
        if (TextUtils.isEmpty(etConfirmPassword.text.toString().trim())) {
            etConfirmPassword.error = getString(R.string.valid_password)
            etConfirmPassword.requestFocus()
            return false
        }
        if (etPassword.text.toString().trim().length < 6 || etConfirmPassword.text.toString().trim().length < 6) {
            etPassword.error = getString(R.string.password_short)
            return false
        }
        if (etPassword.text.toString().trim() != etConfirmPassword.text.toString().trim()) {
            etConfirmPassword.error = getString(R.string.password_do_not_match)
            return false
        }
        return true
    }

    private fun callSignUpApi() {
        progressDialog?.show()

        try {
            val mLoginRequest = LoginRequest()
            mLoginRequest.email = etEmail.text.toString().trim()
            mLoginRequest.password = etPassword.text.toString().trim()
            mLoginRequest.userType = "attendee"

            ApiClient.getClient()?.create(ApiInterface::class.java)?.signUp(mLoginRequest)
                ?.enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(
                        call: Call<ResponseBody>?, response: Response<ResponseBody>
                    ) {
                        progressDialog?.dismiss()
                        try {
                            val gson = Gson()
                            if (response.body() != null && response.isSuccessful) {
                                val mResponse = gson.fromJson(
                                    response.body()!!.string(), GeneralResponse::class.java
                                )
                                val header = response.headers().get("x-auth")
                                SharedPrefsHelper.getInstance().save("x-auth", header)
                                SharedPrefsHelper.getInstance().save("isProfileSetup", false)

                                SharedPrefsHelper.getInstance().save("userID",mLoginRequest.email )

                                SharedPrefsHelper.getInstance()
                                    .save("isEmailVerified", false)
                                DialogUtils.showAlertDialog(
                                    this@SignUpActivity,
                                    mResponse.getMessage()!!,
                                    object : AppCallBackListner.DialogClickCallback {
                                        override fun onButtonClick() {
                                            val intent = Intent(
                                                this@SignUpActivity,
                                                OTPVerificationActivity::class.java
                                            )
                                            intent.putExtra("email", etEmail.text.toString().trim())
                                            startActivity(intent)
                                            finish()
                                        }
                                    })
                            } else if (response.errorBody() != null) {
                                val error = gson.fromJson(
                                    response.errorBody()!!.string(),
                                    GeneralResponse::class.java
                                )
                                if (error.getMessage() != null) {
                                    DialogUtils.showAlertDialog(
                                        this@SignUpActivity, error.getMessage()!!, null
                                    )
                                } else {
                                    AppUtils.showToast(
                                        findViewById(android.R.id.content),
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
                        progressDialog?.dismiss()
                        AppUtils.showToast(
                            findViewById(android.R.id.content),
                            getString(R.string.somethingWrong)
                        )
                    }
                })

        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }
}
