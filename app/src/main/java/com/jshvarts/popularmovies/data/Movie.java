package com.jshvarts.popularmovies.data;

/**
 * POJO that represents the movie summary.
 */
public class Movie {
    private final int id;

    private final String posterPath;

    public Movie(int id, String posterPath) {
        this.id = id;
        this.posterPath = posterPath;
    }

    public int getId() {
        return id;
    }

    public String getPosterPath() {
        return posterPath;
    }
}
