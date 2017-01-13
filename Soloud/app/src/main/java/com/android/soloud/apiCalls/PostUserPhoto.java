package com.android.soloud.apiCalls;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by f.stamopoulos on 14/12/2016.
 */

public interface PostUserPhoto {

    @Multipart
    @POST("/api/Posts/ByImage?Caption=yololeilo")
    Call<ResponseBody> postImage(@Header("Authorization") String soLoudToken,
                                 @Part MultipartBody.Part image,
                                 @Part("description") RequestBody description);


}
