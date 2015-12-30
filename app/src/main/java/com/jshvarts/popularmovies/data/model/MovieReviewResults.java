package com.jshvarts.popularmovies.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Container that holds a list of movie reviews.
 */
public class MovieReviewResults {
    @SerializedName("results")
    private final List<MovieReview> reviews;

    public MovieReviewResults(List<MovieReview> reviews) {
        this.reviews = reviews;
    }
    public List<MovieReview> getReviews() {
        return reviews;
    }
}
