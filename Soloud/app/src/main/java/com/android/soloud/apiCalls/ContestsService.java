package com.android.soloud.apiCalls;

import com.android.soloud.models.Contest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

/**
 * Created by f.stamopoulos on 5/11/2016.
 */

public interface ContestsService {
    @GET("/api/Contests?$expand=User,ProductPhotos,ExamplePhotos")
    Call<List<Contest>> getContests(@Header("Authorization") String BearerAndSoLoudToken);
}

