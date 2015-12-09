package com.jshvarts.popularmovies.application.di;

import com.jshvarts.popularmovies.MovieListActivity;
import com.jshvarts.popularmovies.MovieListFragment;
import com.jshvarts.popularmovies.application.PopularMoviesApplication;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Dagger component which lists all injection points of the app. Any class that
 * injects an object via Dagger must have a corresponding inject() method here.
 */

@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {

    void inject(PopularMoviesApplication application);
    void inject(MovieListFragment fragment);

}
