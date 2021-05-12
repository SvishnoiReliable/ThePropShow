package com.ongraph.realestate.ui.activities

import android.app.ProgressDialog
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.gson.Gson
import com.ongraph.realestate.R
import com.ongraph.realestate.bean.request.LoginRequest
import com.ongraph.realestate.bean.response.GeneralResponse
import com.ongraph.realestate.callbacks.AppCallBackListner
import com.ongraph.realestate.otp.AppSignatureHashHelper
import com.ongraph.realestate.otp.SMSReceiver
import com.ongraph.realestate.rest.ApiClient
import com.ongraph.realestate.rest.ApiInterface
import com.ongraph.realestate.utils.AppUtils
import com.ongraph.realestate.utils.DialogUtils
import com.ongraph.realestate.utils.SharedPrefsHelper
import kotlinx.android.synthetic.main.activity_otp_verification.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OTPVerificationActivity : BaseActivity(), SMSReceiver.OTPReceiveListener {

    private var progressDialog: ProgressDialog? = null
    private var otpCode = ""
    private var smsReceiver: SMSReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp_verification)

        initViews()
    }

    private fun initViews() {
        progressDialog = ProgressDialog(this)
        progressDialog!!.setMessage("Please wait")
        progressDialog!!.setCancelable(false)

        try {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                AppUtils.spannableText(
                    tvResend,
                    getString(R.string.resend_code),
                    "Resend",
                    getColor(R.color.yellow)
                )
            }

            if (intent != null && intent.getStringExtra("email") != null) {
                tvVerifyText.text =
                    getString(R.string.enter_otp) + " " + intent.getStringExtra("email")

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    AppUtils.spannableText(
                        tvVerifyText,
                        tvVerifyText.text.toString().trim(),
                        intent.getStringExtra("email"),
                        getColor(R.color.yellow)
                    )
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        val appSignatureHashHelper = AppSignatureHashHelper(this)

        Log.i("OTPVerificationActivity", "HashKey: " + appSignatureHashHelper.appSignatures[0])
//        startSMSListener()

        btnConfirm.setOnClickListener {
            AppUtils.hideKeyboard(this)
            if (validate()) {
                if (AppUtils.isConnected(this)) {
                    if (otpCode.isEmpty()) {
                        otpCode = etCode1.text.toString().trim() + etCode2.text.toString().trim() +
                                etCode3.text.toString().trim() + etCode4.text.toString().trim() +
                                etCode5.text.toString().trim()
                        verifyOtpApi()
                    } else {
                        verifyOtpApi()
                    }
                } else {
                    AppUtils.showToast(
                        findViewById(android.R.id.content),
                        getString(R.string.chk_network)
                    )
                }
            }
        }

        tvResend.setOnClickListener {
            if (AppUtils.isConnected(this)) {
                resendEmail()
            } else {
                AppUtils.showToast(
                    findViewById(android.R.id.content),
                    getString(R.string.chk_network)
                )
            }
        }

        etCode1.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (etCode1.text.toString().trim().length == 1) {
                    etCode1.setBackgroundResource(R.drawable.rounded_corners_white)
                    etCode1.setTextColor(resources.getColor(R.color.yellow))
                    etCode2.requestFocus()
                } else {
                    etCode1.setBackgroundResource(R.drawable.rounded_corners_grey)
                    etCode1.setTextColor(resources.getColor(R.color.white))
                }
            }
        })
        etCode2.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (etCode2.text.toString().trim().length == 1) {
                    etCode2.setBackgroundResource(R.drawable.rounded_corners_white)
                    etCode2.setTextColor(resources.getColor(R.color.yellow))
                    etCode3.requestFocus()
                } else {
                    etCode2.setBackgroundResource(R.drawable.rounded_corners_grey)
                    etCode2.setTextColor(resources.getColor(R.color.white))
                }
            }
        })
        etCode3.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (etCode3.text.toString().trim().length == 1) {
                    etCode3.setBackgroundResource(R.drawable.rounded_corners_white)
                    etCode3.setTextColor(resources.getColor(R.color.yellow))
                    etCode4.requestFocus()
                } else {
                    etCode3.setBackgroundResource(R.drawable.rounded_corners_grey)
                    etCode3.setTextColor(resources.getColor(R.color.white))
                }
            }
        })
        etCode4.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (etCode4.text.toString().trim().length == 1) {
                    etCode4.setBackgroundResource(R.drawable.rounded_corners_white)
                    etCode4.setTextColor(resources.getColor(R.color.yellow))
                    etCode5.requestFocus()
                } else {
                    etCode4.setBackgroundResource(R.drawable.rounded_corners_grey)
                    etCode4.setTextColor(resources.getColor(R.color.white))
                }
            }
        })
        etCode5.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (etCode5.text.toString().trim().length == 1) {
                    etCode5.setBackgroundResource(R.drawable.rounded_corners_white)
                    etCode5.setTextColor(resources.getColor(R.color.yellow))
                    AppUtils.hideKeyboard(this@OTPVerificationActivity)
                } else {
                    etCode5.setBackgroundResource(R.drawable.rounded_corners_grey)
                    etCode5.setTextColor(resources.getColor(R.color.white))
                }
            }
        })

    }

    private fun startSMSListener() {
        try {
            smsReceiver = SMSReceiver()
            smsReceiver!!.setOTPListener(this)

            val intentFilter = IntentFilter()
            intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION)
            this.registerReceiver(smsReceiver, intentFilter)

            val client = SmsRetriever.getClient(this)

            val task = client.startSmsRetriever()
            task.addOnSuccessListener {
                //                AppUtils.showToast(findViewById(android.R.id.content), "onSuccess")
            }
            task.addOnFailureListener {
                //                AppUtils.showToast(findViewById(android.R.id.content), "onFailure")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun validate(): Boolean {
        if (TextUtils.isEmpty(etCode1.text.toString().trim()) ||
            TextUtils.isEmpty(etCode2.text.toString().trim()) ||
            TextUtils.isEmpty(etCode3.text.toString().trim()) ||
            TextUtils.isEmpty(etCode4.text.toString().trim()) ||
            TextUtils.isEmpty(etCode5.text.toString().trim())
        ) {
            AppUtils.showToast(findViewById(android.R.id.content), getString(R.string.valid_otp))
            return false
        }
        return true
    }

    override fun onOTPReceived(otp: String) {
        otpCode = otp

        etCode1.setText(otpCode[0].toString())
        etCode2.setText(otpCode[1].toString())
        etCode3.setText(otpCode[2].toString())
        etCode4.setText(otpCode[3].toString())
        etCode5.setText(otpCode[4].toString())
        if (smsReceiver != null) {
            unregisterReceiver(smsReceiver)
            smsReceiver = null
        }
    }

    override fun onOTPTimeOut() {
        AppUtils.showToast(findViewById(android.R.id.content), getString(R.string.time_out))
    }

    override fun onOTPReceivedError(error: String) {
        AppUtils.showToast(findViewById(android.R.id.content), error)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (smsReceiver != null) {
            unregisterReceiver(smsReceiver)
        }
    }

    private fun verifyOtpApi() {
        progressDialog?.show()
        val mLoginRequest = LoginRequest()
        mLoginRequest.otp = otpCode.toInt()

        ApiClient.getClient()?.create(ApiInterface::class.java)?.verifyEmail(mLoginRequest)
            ?.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    progressDialog?.dismiss()
                    try {
                        val gson = Gson()
                        if (response.body() != null && response.isSuccessful) {
                            val mResponse =
                                gson.fromJson(
                                    response.body()!!.string(), GeneralResponse::class.java
                                )
                            DialogUtils.showAlertDialog(
                                this@OTPVerificationActivity,
                                mResponse.getMessage()!!,
                                object : AppCallBackListner.DialogClickCallback {
                                    override fun onButtonClick() {
                                        try {
                                            SharedPrefsHelper.getInstance()
                                                .save("isEmailVerified", true)
                                            val intent = Intent(
                                                this@OTPVerificationActivity,
                                                CompleteProfileActivity::class.java
                                            )
                                            startActivity(intent)
                                            finish()
                                        } catch (e: java.lang.Exception) {
                                            e.printStackTrace()
                                        }
                                    }
                                })
                        } else if (response.errorBody() != null) {
                            val error = gson.fromJson(
                                response.errorBody()!!.string(), GeneralResponse::class.java
                            )

                            SharedPrefsHelper.getInstance()
                                .save("isEmailVerified", false)
                            if (error.getMessage() != null) {
                                DialogUtils.showAlertDialog(
                                    this@OTPVerificationActivity,
                                    error.getMessage()!!, null
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
    }

    private fun resendEmail() {
        progressDialog?.show()
        ApiClient.getClient()?.create(ApiInterface::class.java)?.resendEmail()
            ?.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>?,
                    response: Response<ResponseBody>?
                ) {
                    try {
                        progressDialog!!.dismiss()
                        val gson = Gson()
                        if (response?.body() != null && response.isSuccessful) {
                            val mResponse = gson.fromJson(
                                response.body()!!.string(),
                                GeneralResponse::class.java
                            )
                            DialogUtils.showAlertDialog(
                                this@OTPVerificationActivity,
                                mResponse.getMessage()!!, null
                            )
                        } else if (response?.errorBody() != null) {
                            val error = gson.fromJson(
                                response.errorBody()!!.string(),
                                GeneralResponse::class.java
                            )
                            if (error.getMessage() != null) {
                                DialogUtils.showAlertDialog(
                                    this@OTPVerificationActivity,
                                    error.getMessage()!!, null
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
                    AppUtils.showToast(
                        findViewById(android.R.id.content),
                        getString(R.string.somethingWrong)
                    )
                }
            })
    }
}