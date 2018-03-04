package com.android.soloud.utils;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import com.android.soloud.login.LoginActivity;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;

/**
 * Created by f.stamopoulos on 9/4/2017.
 */

public class LogoutHelper {

    private Context mContext;

    public LogoutHelper(Context mContext) {
        this.mContext = mContext;
    }

    public void logOut(){
        clearSharedPreferences();
        logOutFromFacebook();
    }

    private void clearSharedPreferences(){
        String[] prefsToDelete = {SharedPrefsHelper.USER_FB_ID, SharedPrefsHelper.FB_TOKEN};
        SharedPrefsHelper.deleteFromPrefs(mContext, prefsToDelete);
    }

    private void logOutFromFacebook(){
        FacebookSdk.sdkInitialize(mContext);
        LoginManager.getInstance().logOut();
        Intent intent = new Intent(mContext, LoginActivity.class);
        mContext.startActivity(intent);
        ((AppCompatActivity)mContext).finish();
    }
}
