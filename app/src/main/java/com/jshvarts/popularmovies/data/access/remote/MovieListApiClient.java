package com.jshvarts.popularmovies.data.access.remote;

import com.jshvarts.popularmovies.data.model.MovieResults;

import javax.inject.Singleton;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * An interface that defines REST urls for populating movie list.
 */
@Singleton
public interface MovieListApiClient {
    @GET("/3/discover/movie")
    Call<MovieResults> movies(@Query("sort_by") String sortBy);
}

