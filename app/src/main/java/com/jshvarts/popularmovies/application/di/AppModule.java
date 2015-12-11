package com.jshvarts.popularmovies.application.di;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.jshvarts.popularmovies.application.ImageUtils;
import com.jshvarts.popularmovies.data.MovieApiClient;
import com.jshvarts.popularmovies.data.RetrofitMovieApiClient;

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
    public MovieApiClient provideMovieApiClient() {
        return new RetrofitMovieApiClient();
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
