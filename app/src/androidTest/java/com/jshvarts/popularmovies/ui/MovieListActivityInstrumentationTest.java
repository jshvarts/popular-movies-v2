package com.jshvarts.popularmovies.ui;

import android.content.Intent;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.jshvarts.popularmovies.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

/**
 * Instrumentation test for the MovieListActivity and its fragments(s).
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class MovieListActivityInstrumentationTest {
    @Rule
    public ActivityTestRule<MovieListActivity> activityTestRule = new ActivityTestRule<>(MovieListActivity.class, true, false);

    @Test
    public void movieGridView_isVisible() {
        launchActivity();

        onView(withId(R.id.movie_list_gridview))
                .check(matches(isDisplayed()));
    }

    @Test
    public void movieListItem_item0isVisible_onInitialLoad() {
        launchActivity();

        onData(anything())
                .inAdapterView(withId(R.id.movie_list_gridview))
                .atPosition(0)
                .check(matches(isDisplayed()));
    }

    @Test
    public void movieListItem_takesUserToDetailPage_onClick() {
        launchActivity();

        onData(anything())
                .inAdapterView(withId(R.id.movie_list_gridview))
                .atPosition(0)
                .perform(click());

        onView(withId(R.id.movie_detail_scrollview))
                .check(matches(isDisplayed()));
    }

    @Test
    public void movieListItem_item15isNotClickable_onInitialLoad() {
        launchActivity();

        onData(anything())
                .inAdapterView(withId(R.id.movie_list_gridview))
                .atPosition(15)
                .check(matches(is(not(isClickable()))));
    }

    @Test
    public void progressBar_isNotInvisible() {
        launchActivity();

        // progress bar can be either VISIBLE or GONE
        onView(withId(R.id.progress_bar))
                .check(matches(not(is(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)))));
    }

    private void launchActivity() {
        activityTestRule.launchActivity(new Intent());
    }
}
