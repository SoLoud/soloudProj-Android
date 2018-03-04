package com.android.soloud.contests;

import com.android.soloud.models.Contest;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

/**
 * Created by f.stamopoulos on 5/11/2016.
 */

interface ContestsApi {

    @GET("/api/Contests?$expand=User,ProductPhotos,ExamplePhotos")
    Call<List<Contest>> getContests(@Header("Authorization") String BearerAndSoLoudToken);


    @GET("trends/place.json")
    Observable<List<Contest>> getContestsRX(@Header("Authorization") String BearerAndSoLoudToken);

}

