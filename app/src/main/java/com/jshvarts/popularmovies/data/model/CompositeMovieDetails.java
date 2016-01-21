package com.jshvarts.popularmovies.data.model;

import java.util.List;

/**
 * Includes movie detail data, review count and trailers needed for the movie detail UI.
 */
public class CompositeMovieDetails {
    public static final int REVIEW_COUNT_PENDING_LOOKUP = -1;
    public static final int REVIEW_COUNT_UNAVAILABLE = 0;

    private MovieDetails movieDetails;
    private List<MovieTrailer> movieTrailerList;
    private int reviewCount = REVIEW_COUNT_PENDING_LOOKUP;

    public MovieDetails getMovieDetails() {
        return movieDetails;
    }

    public void setMovieDetails(MovieDetails movieDetails) {
        this.movieDetails = movieDetails;
    }

    public List<MovieTrailer> getMovieTrailerList() {
        return movieTrailerList;
    }

    public void setMovieTrailerList(List<MovieTrailer> movieTrailerList) {
        this.movieTrailerList = movieTrailerList;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }
}
