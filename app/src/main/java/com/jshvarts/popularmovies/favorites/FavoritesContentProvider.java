package com.jshvarts.popularmovies.favorites;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;
import java.sql.SQLException;

import static com.jshvarts.popularmovies.favorites.FavoritesProviderConstants.PROVIDER_NAME;
import static com.jshvarts.popularmovies.favorites.FavoritesProviderConstants.CONTENT_URI;

/**
 * Content Provider implementation for Pop Movies.
 */
public class FavoritesContentProvider extends ContentProvider {

    private static final int FAVORITES = 1;
    private static final int FAVORITE_ID = 2;
    private static final UriMatcher uriMatcher = getUriMatcher();

    private static UriMatcher getUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "favorites", FAVORITES);
        uriMatcher.addURI(PROVIDER_NAME, "favorites/#", FAVORITE_ID);
        return uriMatcher;
    }

    private final String LOG_TAG = getClass().getSimpleName();

    FavoritesDbHelper db;

    @Override
    public boolean onCreate() {
        db=new FavoritesDbHelper(getContext());
        return (db == null) ? false : true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        String id = null;

        if (uriMatcher.match(uri) == FAVORITE_ID) {
            //this query is for one single favorite. Get the id from the URI.
            id = uri.getPathSegments().get(1);
        }

        return db.getFavorites(id, projection, selection, selectionArgs, sortOrder);
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case FAVORITES:
                return "vnd.android.cursor.dir/vnd.com.jshvarts.popularmovies.provider.favorites";
            case FAVORITE_ID:
                return "vnd.android.cursor.item/vnd.com.jshvarts.popularmovies.provider.favorites";
            default:
                return null;
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long id;
        try {
            id = db.addFavorite(values);
        } catch(SQLException e) {
            Log.e(LOG_TAG, "error inserting a favorite. " + e.getMessage());
            return null;
        }
        return ContentUris.withAppendedId(CONTENT_URI, id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        String id = null;
        if(uriMatcher.match(uri) == FAVORITE_ID) {
            //delete is for one single favorite. Get the id from the URI.
            id = uri.getPathSegments().get(1);
        }

        int count = db.deleteFavorites(id);
        if (count == 0) {
            Log.e(LOG_TAG, "Failed to delete a favorite " + uri);
        }
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        String id = null;
        if(uriMatcher.match(uri) == FAVORITE_ID) {
            //update is for one single favorite. Get the id from the URI.
            id = uri.getPathSegments().get(1);
        }

        int count = db.updateFavorites(id, values);
        if (count == 0) {
            Log.e(LOG_TAG, "Failed to update a favorite " + uri);
        }
        return count;
    }
}
