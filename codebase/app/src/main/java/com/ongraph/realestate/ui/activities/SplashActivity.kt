package com.ongraph.realestate.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import com.ongraph.realestate.R
import com.ongraph.realestate.utils.SharedPrefsHelper

class SplashActivity : BaseActivity() {

    @SuppressLint("HardwareIds")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        showDefaultSplashFlow()
    }

    private fun showDefaultSplashFlow() {

        Handler().postDelayed({
            if (SharedPrefsHelper.getInstance().get<Any>("x-auth") != null) {
                if (SharedPrefsHelper.getInstance()
                        .get<Any>("isEmailVerified") != null && SharedPrefsHelper.getInstance()
                        .get("isEmailVerified")
                ) {
                    if (SharedPrefsHelper.getInstance()
                            .get<Any>("isProfileSetup") != null && SharedPrefsHelper.getInstance()
                            .get("isProfileSetup")
                    ) {
                        val mIntent = Intent(this, HomeActivity::class.java)
                        startActivity(mIntent)
                        finish()
                    } else {
                        val mIntent = Intent(this, CompleteProfileActivity::class.java)
                        startActivity(mIntent)
                        finish()
                    }
                } else {
                    val mIntent = Intent(this, OTPVerificationActivity::class.java)
                    startActivity(mIntent)
                    finish()
                }

            } else {
                val mIntent = Intent(this, LoginActivity::class.java)
                startActivity(mIntent)
                finish()
            }
        }, 2000)
    }
}
