package com.android.soloud.apiCalls;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created by f.stamopoulos on 11/12/2016.
 */

public interface PostService {

    @POST("/api/posts")
    Call<Object> sendUserPostToBackend(@Header("Authorization") String soLoudToken,
                                       @Body String description);
}
