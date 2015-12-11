package com.jshvarts.popularmovies.data;

import javax.inject.Singleton;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * An interface that defines REST urls for this app.
 */
@Singleton
public interface MovieApiClient {
    @GET("/3/discover/movie")
    Call<MovieResults> movies(@Query("sort_by") String sortBy);

    @GET("/3/movie/{id}")
    Call<MovieDetails> movie(@Path("id") String id);
}

