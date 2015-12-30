package com.jshvarts.popularmovies.data;

/**
 * Container that holds a count of total movie reviews.
 */
public class MovieReviewCountResults {
    private final int totalResults;

    public MovieReviewCountResults(int totalResults) {
        this.totalResults = totalResults;
    }
    public int getTotalResults() {
        return totalResults;
    }
}
