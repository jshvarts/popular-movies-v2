package com.jshvarts.popularmovies.ui;

import com.jshvarts.popularmovies.BuildConfig;
import com.jshvarts.popularmovies.application.ImageUtils;
import com.jshvarts.popularmovies.application.TestPopularMoviesApplication;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, application = TestPopularMoviesApplication.class)
public class MovieListActivityTest {

    private final static String SAMPLE_POSTER_PATH = "/nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg";
    private final static String SAMPLE_IMAGE_URL = "http://image.tmdb.org/t/p/w185" + SAMPLE_POSTER_PATH;

    @Test
    public void testActivityLifecycle() {
        Robolectric.buildActivity(MovieListActivity.class)
                .create()
                .start()
                .resume()
                .visible()
                .get();
    }

    @Test
    public void testImageUrlGeneration() {
        ImageUtils imageUtils = new ImageUtils(RuntimeEnvironment.application);
        Assert.assertEquals(SAMPLE_IMAGE_URL, imageUtils.getImageUrl(SAMPLE_POSTER_PATH));
    }
}
