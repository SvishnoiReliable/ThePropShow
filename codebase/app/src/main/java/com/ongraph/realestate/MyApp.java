package com.ongraph.realestate;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.multidex.MultiDexApplication;

import com.ongraph.realestate.chatModule.Constants;
import com.ongraph.realestate.chatModule.FileUtil;
import com.ongraph.realestate.chatModule.rtc.AgoraEventHandler;
import com.ongraph.realestate.chatModule.rtc.EngineConfig;
import com.ongraph.realestate.chatModule.rtc.EventHandler;
import com.ongraph.realestate.chatModule.rtmtutorial.ChatManager;
import com.ongraph.realestate.chatModule.stats.StatsManager;
import com.ongraph.realestate.utils.SharedPrefsHelper;

import io.agora.rtc.RtcEngine;

public class MyApp extends MultiDexApplication {

    private RtcEngine mRtcEngine;
    private EngineConfig mGlobalConfig = new EngineConfig();
    private AgoraEventHandler mHandler = new AgoraEventHandler();
    private StatsManager mStatsManager = new StatsManager();

    private static MyApp sInstance;
    private ChatManager mChatManager;


    public static MyApp the() {
        return sInstance;
    }

    private static MyApp base;

    public static Context getAppContext() {
        return base;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        base = this;

        sInstance = this;

        try {
            mRtcEngine = RtcEngine.create(getApplicationContext(), getString(R.string.private_app_id), mHandler);
            // Sets the channel profile of the Agora RtcEngine.
            // The Agora RtcEngine differentiates channel profiles and applies different optimization algorithms accordingly. For example, it prioritizes smoothness and low latency for a video call, and prioritizes video quality for a video broadcast.
            mRtcEngine.setChannelProfile(io.agora.rtc.Constants.CHANNEL_PROFILE_LIVE_BROADCASTING);
            mRtcEngine.enableVideo();
            mRtcEngine.setLogFile(FileUtil.initializeLogFile(this));
        } catch (Exception e) {
            e.printStackTrace();
        }

        initConfig();

        mChatManager = new ChatManager(this);
        mChatManager.init();

    }


    public ChatManager getChatManager() {
        return mChatManager;
    }


    private void initConfig() {
        SharedPreferences pref = SharedPrefsHelper.getPreferences(getApplicationContext());
        mGlobalConfig.setVideoDimenIndex(pref.getInt(
                Constants.PREF_RESOLUTION_IDX, Constants.DEFAULT_PROFILE_IDX));

        boolean showStats = pref.getBoolean(Constants.PREF_ENABLE_STATS, false);
        mGlobalConfig.setIfShowVideoStats(showStats);
        mStatsManager.enableStats(showStats);

        mGlobalConfig.setMirrorLocalIndex(pref.getInt(Constants.PREF_MIRROR_LOCAL, 0));
        mGlobalConfig.setMirrorRemoteIndex(pref.getInt(Constants.PREF_MIRROR_REMOTE, 0));
        mGlobalConfig.setMirrorEncodeIndex(pref.getInt(Constants.PREF_MIRROR_ENCODE, 0));
    }

    public EngineConfig engineConfig() {
        return mGlobalConfig;
    }

    public RtcEngine rtcEngine() {
        return mRtcEngine;
    }

    public StatsManager statsManager() {
        return mStatsManager;
    }

    public void registerEventHandler(EventHandler handler) {
        mHandler.addHandler(handler);
    }

    public void removeEventHandler(EventHandler handler) {
        mHandler.removeHandler(handler);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        RtcEngine.destroy();
    }
}