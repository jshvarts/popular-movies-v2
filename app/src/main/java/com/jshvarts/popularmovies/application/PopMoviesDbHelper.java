package com.jshvarts.popularmovies.application;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Database helper for the app.
 */
public class PopMoviesDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME="favorite_movides.db";
    private static final int SCHEMA=1;
    public static final String TABLE_FAVORITES = "favorites";

    public static final String MOVIE_ID = "movie_id";
    public static final String MOVIE_TITLE = "movie_title";
    public static final String POSTER_PATH = "poster_path";

    public static final String QUERY_FAVORITES = "SELECT " +
            MOVIE_ID +
            ", " +
            MOVIE_TITLE +
            ", " +
            POSTER_PATH +
            " FROM " +
            TABLE_FAVORITES +
            " order by " +
            MOVIE_TITLE +
            " asc";

    public static final String QUERY_FAVORITE_BY_ID = "SELECT " +
            MOVIE_ID +
            " FROM " +
            TABLE_FAVORITES +
            " where movie_id = ?";

    public PopMoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, SCHEMA);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(getCreateTableSql());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        throw new RuntimeException("Upgrade functionality has not been implemented");
    }

    private String getCreateTableSql() {
        StringBuilder sql = new StringBuilder();
        sql.append("CREATE TABLE ");
        sql.append(TABLE_FAVORITES);
        sql.append(" (");
        sql.append(MOVIE_ID);
        sql.append(" INT PRIMARY KEY, ");
        sql.append(MOVIE_TITLE);
        sql.append(" TEXT, ");
        sql.append(POSTER_PATH);
        sql.append(" TEXT)");

        return sql.toString();
    }
}
