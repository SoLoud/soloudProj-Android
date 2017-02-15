package com.android.soloud.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.soloud.R;


/**
 * Created by f.stamopoulos on 11/12/2016.
 */

public class SharedPrefsHelper {

    public static final String FB_TOKEN = "fbtoken";
    public static final String USER_FB_ID = "userid" ;
    public static final String USER_NAME = "username";
    public static final String USER_PROFILE_PICTURE_URL = "userprofilepicture";
    public static final String SOLOUD_TOKEN = "sltoken";
    public static final String POST_POP_UP_DISPLAYED = "post_pop_up_displayed";

    public static boolean getBooleanFromPrefs(Context context, String PREF_CONSTANT){
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        return  sharedPref.getBoolean(PREF_CONSTANT, false);
    }

    public static void storeBooleanInPrefs(Context context, boolean prefValue, String PREF_CONSTANT){
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(PREF_CONSTANT, prefValue);
        editor.apply();
    }

    public static String getFromPrefs(Context context, String PREF_CONSTANT){
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        return  sharedPref.getString(PREF_CONSTANT, null);
    }

    public static void storeInPrefs(Context context, String prefValue, String PREF_CONSTANT){
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(PREF_CONSTANT, prefValue);
        editor.apply();
    }

    public static void deleteFromPrefs(Context context, String[] pref_constants_array){
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        for (int i=0; i< pref_constants_array.length -1; i++){
            editor.remove(pref_constants_array[i]).apply();
        }
        /*editor.remove(USER_FB_ID).apply();
        editor.remove(FB_TOKEN).apply();*/
        editor.clear().apply();
    }

}
