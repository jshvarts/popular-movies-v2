package com.jshvarts.popularmovies.ui;

import com.jshvarts.popularmovies.BuildConfig;
import com.jshvarts.popularmovies.application.TestPopularMoviesApplication;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, application = TestPopularMoviesApplication.class)
public class SettingActivityTest {

    @Test
    public void testActivityLifecycle() {
        Robolectric.buildActivity(SettingsActivity.class)
                .create()
                .start()
                .resume()
                .visible()
                .get();
    }
}
