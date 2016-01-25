package com.jshvarts.popularmovies.favorites;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Constants used by the current provider and its clients.
 */
public class FavoritesProviderConstants {
    public static final String COLUMN_MOVIE_ID = "movie_id";
    public static final String COLUMN_MOVIE_TITLE = "movie_title";
    public static final String COLUMN_POSTER_PATH = "poster_path";
    public static final String PROVIDER_NAME = "com.jshvarts.popularmovies.favorites";
    public static final Uri CONTENT_URI = Uri.parse("content://" + PROVIDER_NAME + "/favorites");
}
