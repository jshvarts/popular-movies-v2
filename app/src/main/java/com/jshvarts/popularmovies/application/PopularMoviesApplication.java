package com.jshvarts.popularmovies.application;

import android.app.Application;
import android.support.annotation.VisibleForTesting;

import com.jshvarts.popularmovies.application.di.AppComponent;
import com.jshvarts.popularmovies.application.di.AppModule;
import com.jshvarts.popularmovies.application.di.DaggerAppComponent;

/**
 * Popular Movies application bootstrap class.
 */
public class PopularMoviesApplication extends Application {

    private AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        //initialize dependency-injection object graph
        appComponent = buildDaggerComponent();
    }

    public AppComponent getDaggerComponent() {
        return appComponent;
    }

    @VisibleForTesting
    public void setDaggerComponent(AppComponent appComponent) {
        this.appComponent = appComponent;
    }

    private AppComponent buildDaggerComponent() {
        return DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
    }
}
