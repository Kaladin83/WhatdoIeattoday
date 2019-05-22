package com.example.maratbe.whatdoieattoday;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.maratbe.whatdoieattoday.interfaces.Constants;

public class MySharedPreferences implements Constants {
    public static final String SP_NAME = "userDetails";
    public SharedPreferences myPreferences;

    public MySharedPreferences(Context context) {
        myPreferences = context.getSharedPreferences(SP_NAME, 0);
    }

    public void storeData(String loggedIn, String userId) {
        SharedPreferences.Editor editor = myPreferences.edit();
        editor.putString("loggedIn", loggedIn);
        editor.putString("userId", userId);
        editor.apply();
    }

    public void getData() {
        MainActivity.getMapOfProperties().put(USER_ID ,myPreferences.getString("userId", "0"));
        MainActivity.getMapOfProperties().put(LOGGED_IN ,myPreferences.getString("loggedIn", OUT));
    }
}
