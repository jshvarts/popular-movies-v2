package com.jshvarts.popularmovies.application;

/**
 * Configures Test Application.
 */
public class TestPopularMoviesApplication extends PopularMoviesApplication {
    protected boolean isInUnitTests() {
        return true;
    }
}
