package com.jshvarts.popularmovies.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Container that holds a list of movies.
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
