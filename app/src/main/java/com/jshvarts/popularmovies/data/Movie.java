package com.jshvarts.popularmovies.data;

import com.google.gson.annotations.SerializedName;

/**
 * Movie object POJO that represents the JSON movie object.
 */
public class Movie {
    private final int id;

    @SerializedName("poster_path")
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
