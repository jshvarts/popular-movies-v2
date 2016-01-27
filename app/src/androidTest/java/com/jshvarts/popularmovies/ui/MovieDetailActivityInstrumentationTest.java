package com.jshvarts.popularmovies.ui;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.jshvarts.popularmovies.R;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.action.ViewActions.click;
import static org.hamcrest.Matchers.anything;

import static android.support.test.espresso.assertion.PositionAssertions.isAbove;
import static android.support.test.espresso.assertion.PositionAssertions.isLeftOf;

/**
 * Instrumentation test for the MovieDetailActivity and its fragment(s).
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class MovieDetailActivityInstrumentationTest {

    private Context context;

    @Rule
    public ActivityTestRule<MovieListActivity> activityTestRule = new ActivityTestRule<>(MovieListActivity.class, true, false);

    @Before
    public void setUp() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
    }

    @Test
    public void allViews_correctVisibility() {
        launchActivity();

        onView(withContentDescription(R.string.title_activity_movie_detail))
                .check(matches(isDisplayed()));

        onView(withId(R.id.movie_detail_scrollview))
                .check(matches(isDisplayed()));

        onView(withId(R.id.original_title))
                .check(matches(isDisplayed()));

        onView(withId(R.id.poster_image))
                .check(matches(isDisplayed()));

        onView(withId(R.id.release_date))
                .check(matches(isDisplayed()));

        onView(withId(R.id.vote_average))
                .check(matches(isDisplayed()));

        onView(withId(R.id.review_count_link))
                .check(matches(isDisplayed()));

        onView(withId(R.id.favorite_button))
                .check(matches(isDisplayed()));

        onView(withId(R.id.overview))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));

        onView(withId(R.id.trailer_heading))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));

        onView(withId(R.id.trailer_links))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));

        onView(withId(R.id.progress_bar))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
    }

    @Test
    public void allViews_correctPosition() {
        launchActivity();

        onView(withId(R.id.original_title))
                .check(isAbove(withId(R.id.poster_image)));

        onView(withId(R.id.poster_image))
                .check(isLeftOf(withId(R.id.release_date)))
                .check(isLeftOf(withId(R.id.vote_average)))
                .check(isLeftOf(withId(R.id.review_count_link)))
                .check(isLeftOf(withId(R.id.favorite_button)));

        onView(withId(R.id.release_date))
                .check(isAbove(withId(R.id.vote_average)));

        onView(withId(R.id.vote_average))
                .check(isAbove(withId(R.id.review_count_link)));

        onView(withId(R.id.review_count_link))
                .check(isAbove(withId(R.id.favorite_button)));

        onView(withId(R.id.favorite_button))
                .check(isAbove(withId(R.id.overview)));

        onView(withId(R.id.overview))
                .check(isAbove(withId(R.id.trailer_heading)));

        onView(withId(R.id.trailer_heading))
                .check(isAbove(withId(R.id.trailer_links)));
    }

    @Test
    public void certainViews_clickable() {
        launchActivity();

        onView(withId(R.id.favorite_button))
                .check(matches(isClickable()));

        onView(withId(R.id.review_count_link))
                .check(matches(isClickable()));
    }

    private void launchActivity() {
        activityTestRule.launchActivity(new Intent());

        onData(anything())
                .inAdapterView(withId(R.id.movie_list_gridview))
                .atPosition(0)
                .check(matches(isDisplayed()))
                .perform(click());
    }
}