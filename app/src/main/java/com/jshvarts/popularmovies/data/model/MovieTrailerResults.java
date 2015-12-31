package com.jshvarts.popularmovies.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Container that holds a list of movies.
 */
public class MovieTrailerResults {
    @SerializedName("results")
    private final List<MovieTrailer> trailers;

    public MovieTrailerResults(List<MovieTrailer> trailers) {
        this.trailers = trailers;
    }
    public List<MovieTrailer> getTrailers() {
        return trailers;
    }
}
