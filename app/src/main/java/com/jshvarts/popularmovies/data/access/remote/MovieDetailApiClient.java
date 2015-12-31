package com.jshvarts.popularmovies.data.access.remote;

import com.jshvarts.popularmovies.data.model.MovieDetails;
import com.jshvarts.popularmovies.data.model.MovieReview;
import com.jshvarts.popularmovies.data.model.MovieReviewCount;
import com.jshvarts.popularmovies.data.model.MovieReviewResults;
import com.jshvarts.popularmovies.data.model.MovieTrailerResults;

import javax.inject.Singleton;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Path;

/**
 * An interface that defines REST urls for retrieving movie details and reviews.
 */
@Singleton
public interface MovieDetailApiClient {
    @GET("/3/movie/{id}")
    Call<MovieDetails> movie(@Path("id") String movieId);

    @GET("/3/movie/{id}/reviews")
    Call<MovieReviewCount> reviewCount(@Path("id") String movieId);

    @GET("/3/movie/{id}/reviews")
    Call<MovieReviewResults> reviews(@Path("id") String movieId);

    @GET("/3/review/{id}")
    Call<MovieReview> review(@Path("id") String reviewId);

    @GET("/3/movie/{id}/videos")
    Call<MovieTrailerResults> trailers(@Path("id") String movieId);
}

