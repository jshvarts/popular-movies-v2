package com.jshvarts.popularmovies.application.di;

import com.jshvarts.popularmovies.ui.ImageAdapter;
import com.jshvarts.popularmovies.ui.MovieDetailFragment;
import com.jshvarts.popularmovies.ui.MovieListFragment;
import com.jshvarts.popularmovies.ui.MovieReviewListFragment;
import com.jshvarts.popularmovies.ui.SettingsActivity;
import com.jshvarts.popularmovies.ui.SettingsFragment;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Dagger component which lists all injection points of the app. Any class that
 * injects an object via Dagger must have a corresponding inject() method here.
 */

@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {

    // activities
    void inject(SettingsActivity activity);

    // fragments
    void inject(MovieListFragment fragment);
    void inject(MovieDetailFragment fragment);
    void inject(SettingsFragment fragment);
    void inject(MovieReviewListFragment fragment);

    // adapters
    void inject(ImageAdapter adapter);
}
