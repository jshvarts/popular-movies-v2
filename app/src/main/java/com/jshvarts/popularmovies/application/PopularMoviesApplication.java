package com.jshvarts.popularmovies.application;

import android.app.Application;
import android.content.Context;
import android.support.annotation.VisibleForTesting;

import com.jshvarts.popularmovies.application.di.AppComponent;
import com.jshvarts.popularmovies.application.di.AppModule;
import com.jshvarts.popularmovies.application.di.DaggerAppComponent;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

/**
 * Popular Movies application bootstrap class.
 */
public class PopularMoviesApplication extends Application {

    private AppComponent appComponent;

    /**
     * LearkCanary method of checking memory leaks in fragments
     */
    private RefWatcher refWatcher;

    @Override
    public void onCreate() {
        super.onCreate();

        //initialize dependency-injection object graph
        appComponent = buildDaggerComponent();

        refWatcher = LeakCanary.install(this);

    }

    public static RefWatcher getRefWatcher(Context context) {
        PopularMoviesApplication application = (PopularMoviesApplication) context.getApplicationContext();
        return application.refWatcher;
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
