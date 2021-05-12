package com.ongraph.realestate.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.ongraph.realestate.MyApp;
import com.ongraph.realestate.bean.response.ProfileResponse;
import com.ongraph.realestate.chatModule.Constants;

public class SharedPrefsHelper {
    private static final String SHARED_PREFS_NAME = "realEstateApp";

    private static SharedPrefsHelper instance;

    private SharedPreferences sharedPreferences;


    public static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
    }

    private SharedPrefsHelper() {
        instance = this;
        sharedPreferences = MyApp.getAppContext().getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized SharedPrefsHelper getInstance() {
        if (instance == null) {
            instance = new SharedPrefsHelper();
        }
        return instance;
    }

    public void delete(String key) {
        if (sharedPreferences.contains(key)) {
            getEditor().remove(key).commit();
        }
    }

    public void clearPrefs() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    public void save(String key, Object value) {
        SharedPreferences.Editor editor = getEditor();
        if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        } else if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        } else if (value instanceof Float) {
            editor.putFloat(key, (Float) value);
        } else if (value instanceof Long) {
            editor.putLong(key, (Long) value);
        } else if (value instanceof String) {
            editor.putString(key, (String) value);
        } else if (value instanceof Enum) {
            editor.putString(key, value.toString());
        } else if (value != null) {
            throw new RuntimeException("Attempting to save non-supported preference");
        }
        editor.commit();
    }

    public <Object> void saveLoginData(Object myObject) {
        SharedPreferences.Editor editor = getEditor();
        Gson gson = new Gson();
        String json = gson.toJson(myObject);
        editor.putString("LoginData", json);
        editor.commit();
    }

    public ProfileResponse.Data getLoginData() {
        Gson gson = new Gson();
        String json = sharedPreferences.getString("LoginData", "");
        ProfileResponse.Data obj = new ProfileResponse.Data();
        try {
            if (json.isEmpty()) {
                return obj;
            } else {
                obj = gson.fromJson(json, ProfileResponse.Data.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) sharedPreferences.getAll().get(key);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key, T defValue) {
        T returnValue = (T) sharedPreferences.getAll().get(key);
        return returnValue == null ? defValue : returnValue;
    }

    public boolean has(String key) {
        return sharedPreferences.contains(key);
    }

    private SharedPreferences.Editor getEditor() {
        return sharedPreferences.edit();
    }
}
