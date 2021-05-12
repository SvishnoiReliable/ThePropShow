package com.ongraph.realestate.ui.activities

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.ongraph.realestate.MyApp
import com.ongraph.realestate.R
import com.ongraph.realestate.chatModule.rtmtutorial.ChatManager
import com.ongraph.realestate.ui.fragments.EventsFragment
import com.ongraph.realestate.ui.fragments.ExploreEventsFragment
import com.ongraph.realestate.ui.fragments.MessageFragment
import com.ongraph.realestate.ui.fragments.ProfileFragment
import com.ongraph.realestate.utils.SharedPrefsHelper
import com.ongraph.realestate.utils.SharedPrefsHelper.getInstance
import io.agora.rtm.ErrorInfo
import io.agora.rtm.ResultCallback
import io.agora.rtm.RtmClient
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : BaseActivity() {

    private val TAG =
        LoginActivity::class.java.simpleName

    private var mUserId: String? = null
    private var mChatManager: ChatManager? = null
    private var mRtmClient: RtmClient? = null
    private var mIsInChat = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        initValues()


    }

    override fun onResume() {
        super.onResume()

        agoraLogin()
    }


    private fun agoraLogin(){
        if(getInstance().get("isProfileSetup", false)){
            mUserId=  SharedPrefsHelper.getInstance().get("userID")!!

        mChatManager = MyApp.the().getChatManager()
        mRtmClient = mChatManager!!.getRtmClient()

        mIsInChat = true
        mRtmClient!!.login(null, mUserId, object : ResultCallback<Void?> {
            override fun onSuccess(responseInfo: Void?) {
                Log.i(TAG, "login success")
                runOnUiThread {
                    /*val intent =
                        Intent(this@LoginActivity, SelectionActivity::class.java)
                    intent.putExtra(MessageUtil.INTENT_EXTRA_USER_ID, mUserId)
                    startActivity(intent)*/
                }
            }

            override fun onFailure(errorInfo: ErrorInfo) {
                Log.i(TAG, "login failed: " + errorInfo.errorCode)
            }
        })

        }
    }


    private fun initValues() {
        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_event -> setFragment(EventsFragment())
                R.id.action_explor -> setFragment(ExploreEventsFragment())
                R.id.action_message -> setFragment(MessageFragment())
                R.id.action_profile -> setFragment(ProfileFragment())
            }
            true
        }
        bottomNavigation.selectedItemId = R.id.action_profile

        if(SharedPrefsHelper.getInstance().loginData.isProfileSetup!!){
            setFragment(ExploreEventsFragment())
        }else
        setFragment(ProfileFragment())
    }

    private fun setFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.container, fragment)
        fragmentTransaction.commit()
    }

    override fun onStop() {
        super.onStop()
        mRtmClient!!.logout(null)
    }
}
