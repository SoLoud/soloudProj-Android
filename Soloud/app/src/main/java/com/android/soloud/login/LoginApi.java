package com.android.soloud.login;

import com.android.soloud.models.User;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created by f.stamopoulos on 7/12/2016.
 */

public interface LoginApi {

    @FormUrlEncoded
    @POST("/Token")
    Call<User> login(@Header("ExternalLoginType") String loginProvider,
                     @Header("ExternalToken") String token,
                     @Field("grant_type") String grand_type);



}
