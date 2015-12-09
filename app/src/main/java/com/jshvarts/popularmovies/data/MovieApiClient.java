package com.jshvarts.popularmovies.data;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * An interface that defines REST urls for this app.
 */
public interface MovieApiClient {
    @GET("/3/discover/movie")
    Call<MovieResults> movies(@Query("sort_by") String sortBy, @Query("api_key") String apiKey);
}

