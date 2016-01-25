package com.jshvarts.popularmovies.favorites;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Constants used by the current provider and its clients.
 */
public final class FavoritesContract {
    public static final String PROVIDER_NAME = "com.jshvarts.popularmovies.favorites";
    public static final Uri CONTENT_URI = Uri.parse("content://" + PROVIDER_NAME + "/favorites");

    // prevent others from instantiating by making the constructor private and empty
    private FavoritesContract() {}

    public static abstract class FavoriteEntry implements BaseColumns {
        public static final String TABLE_NAME = "favorites";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_MOVIE_TITLE = "movie_title";
        public static final String COLUMN_POSTER_PATH = "poster_path";
    }
}
