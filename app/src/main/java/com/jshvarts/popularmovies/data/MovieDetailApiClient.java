package com.jshvarts.popularmovies.data;

import javax.inject.Singleton;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Path;

/**
 * An interface that defines REST urls for retrieving movie details.
 */
@Singleton
public interface MovieDetailApiClient {
    @GET("/3/movie/{id}")
    Call<MovieDetails> movie(@Path("id") String id);
}

