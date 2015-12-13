package com.jshvarts.popularmovies.ui;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.jshvarts.popularmovies.R;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Instrumentation test for the MovieDetailActivity and its fragment(s).
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class MovieDetailActivityInstrumentationTest {

    private final int SAMPLE_MOVIE_ID = 244786;

    private Context context;

    @Rule
    public ActivityTestRule<MovieDetailActivity> activityTestRule = new ActivityTestRule<>(MovieDetailActivity.class, true, false);

    @Before
    public void setUp() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
     }

    @Test
    public void movieDetailView_isVisible() {
        launchActivity();

        onView(withId(R.id.movie_detail_scrollview))
                .check(matches(isDisplayed()));
    }

    @Test
    public void movieReleaseDate_isVisible() {
        launchActivity();

        onView(withId(R.id.movie_detail_scrollview))
                .check(matches(isDisplayed()));
    }

    private void launchActivity() {
        Intent intent = new Intent(context, MovieDetailActivity.class);
        // Whiplash
        intent.putExtra(MovieDetailActivity.MOVIE_ID_EXTRA, String.valueOf(SAMPLE_MOVIE_ID));
        activityTestRule.launchActivity(intent);
    }
}
