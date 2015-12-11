package com.jshvarts.popularmovies.application.di;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.jshvarts.popularmovies.application.ImageUtils;
import com.jshvarts.popularmovies.data.MovieApiClient;
import com.jshvarts.popularmovies.data.RetrofitMovieApiClient;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;

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
    public OkHttpClient provideOkHttpClient() {
        return new OkHttpClient();
    }

    @Provides
    @Singleton
    public HttpLoggingInterceptor provideLoggingInterceptor() {
        return new HttpLoggingInterceptor();
    }

    @Provides
    @Singleton
    public MovieApiClient provideMovieApiClient(OkHttpClient client, HttpLoggingInterceptor interceptor) {
        return new RetrofitMovieApiClient(client, interceptor);
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
