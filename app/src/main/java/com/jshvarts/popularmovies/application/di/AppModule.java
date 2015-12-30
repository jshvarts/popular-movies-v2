package com.jshvarts.popularmovies.application.di;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.jshvarts.popularmovies.application.ImageUtils;
import com.jshvarts.popularmovies.data.MovieDetailApiClient;
import com.jshvarts.popularmovies.data.MovieListApiClient;
import com.jshvarts.popularmovies.data.RetrofitMovieDetailApiClient;
import com.jshvarts.popularmovies.data.RetrofitMovieListApiClient;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Dagger dependency injection module.
 */
@Module
public class AppModule {
    Context appContext;

    public AppModule(Application application) {
        appContext = application.getApplicationContext();
    }

    @Provides
    @Singleton
    public MovieListApiClient provideMovieListApiClient() {
        return new RetrofitMovieListApiClient();
    }

    @Provides
    @Singleton
    public MovieDetailApiClient provideMovieDetailApiClient() {
        return new RetrofitMovieDetailApiClient();
    }

    @Provides
    @Singleton
    public SharedPreferences provideSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(appContext);
    }

    @Provides
    @Singleton
    public ImageUtils provideUtils() {
        return new ImageUtils(appContext);
    }
}
