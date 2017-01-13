package com.android.soloud.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by f.stamopoulos on 7/12/2016.
 */

public class User {

    @SerializedName("access_token")
    String soloudToken;

    public String getSoloudToken() {
        return soloudToken;
    }
}
