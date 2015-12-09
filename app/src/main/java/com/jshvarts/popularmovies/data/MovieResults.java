package com.jshvarts.popularmovies.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Movie results container POJO that represents the JSON object.
 */
public class MovieResults {
    @SerializedName("results")
    private final List<Movie> movies;

    public MovieResults(List<Movie> movies) {
        this.movies = movies;
    }
    public List<Movie> getMovies() {
        return movies;
    }
}
