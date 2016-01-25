package com.jshvarts.popularmovies.favorites;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.text.TextUtils;

import java.sql.SQLException;

import static com.jshvarts.popularmovies.favorites.FavoritesProviderConstants.COLUMN_MOVIE_ID;
import static com.jshvarts.popularmovies.favorites.FavoritesProviderConstants.COLUMN_MOVIE_TITLE;
import static com.jshvarts.popularmovies.favorites.FavoritesProviderConstants.COLUMN_POSTER_PATH;

/**
 * Database helper for the app.
 */
public class FavoritesDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME="favorites.db";
    private static final int SCHEMA=1;
    public static final String TABLE_FAVORITES = "favorites";

    private static final String SQL_CREATE = "CREATE TABLE " + TABLE_FAVORITES +
            " ("+ COLUMN_MOVIE_ID +" INTEGER PRIMARY KEY, "+ COLUMN_MOVIE_TITLE +" TEXT, "+ COLUMN_POSTER_PATH +" TEXT)";

    private static final String SQL_DROP = "DROP TABLE IS EXISTS " + TABLE_FAVORITES;

    private static final String SQL_FIND_TABLE = "SELECT name FROM sqlite_master WHERE type='table' " +
            "AND name='"+TABLE_FAVORITES+"'";

    private static final String DEFAULT_SORT_ORDER = COLUMN_MOVIE_TITLE;

    public FavoritesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, SCHEMA);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Cursor c = db.rawQuery(SQL_FIND_TABLE, null);

        if (c.getCount() == 0) {
            db.execSQL(SQL_CREATE);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DROP);
        onCreate(db);
    }

    public Cursor getFavorites(String id, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder sqliteQueryBuilder = new SQLiteQueryBuilder();
        sqliteQueryBuilder.setTables(TABLE_FAVORITES);

        if (id != null) {
            sqliteQueryBuilder.appendWhere(COLUMN_MOVIE_ID + " = " + id);
        }

        if (TextUtils.isEmpty(sortOrder)) {
            sortOrder = DEFAULT_SORT_ORDER;
        }

        Cursor cursor = sqliteQueryBuilder.query(getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);

        return cursor;
    }

    public long addFavorite(ContentValues values) throws SQLException {
        long id = getWritableDatabase().insert(TABLE_FAVORITES, "", values);
        if(id <= 0 ) {
            throw new SQLException("Failed to add a favorite");
        }

        return id;
    }

    public int deleteFavorites(String id) {
        if(id == null) {
            return getWritableDatabase().delete(TABLE_FAVORITES, null , null);
        } else {
            return getWritableDatabase().delete(TABLE_FAVORITES, COLUMN_MOVIE_ID +"=?", new String[]{id});
        }
    }

    public int updateFavorites(String id, ContentValues values) {
        if(id == null) {
            return getWritableDatabase().update(TABLE_FAVORITES, values, null, null);
        } else {
            return getWritableDatabase().update(TABLE_FAVORITES, values, COLUMN_MOVIE_ID +"=?", new String[]{id});
        }
    }
}
