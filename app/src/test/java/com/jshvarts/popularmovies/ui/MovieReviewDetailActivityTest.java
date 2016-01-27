package com.jshvarts.popularmovies.ui;

import android.content.Intent;

import com.jshvarts.popularmovies.BuildConfig;
import com.jshvarts.popularmovies.application.TestPopularMoviesApplication;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, application = TestPopularMoviesApplication.class)
public class MovieReviewDetailActivityTest {

    private static final String SAMPLE_REVIEW_ID = "563ed3f9c3a3681b4d034302";
    Intent intent;

    @Before
    public void setUp() {
        intent = new Intent(Intent.ACTION_VIEW);
        intent.putExtra(MovieReviewDetailActivity.REVIEW_ID_EXTRA, SAMPLE_REVIEW_ID);
    }

    @Test
    public void testActivityLifecycle() {
        Robolectric.buildActivity(MovieReviewDetailActivity.class)
                .withIntent(intent)
                .create()
                .start()
                .resume()
                .visible()
                .get();
    }
}
