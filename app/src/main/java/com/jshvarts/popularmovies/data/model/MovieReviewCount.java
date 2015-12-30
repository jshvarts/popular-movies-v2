package com.jshvarts.popularmovies.data.model;

/**
 * Container that holds a count of total movie reviews.
 */
public class MovieReviewCount {
    private final int totalResults;

    public MovieReviewCount(int totalResults) {
        this.totalResults = totalResults;
    }
    public int getTotalResults() {
        return totalResults;
    }
}
