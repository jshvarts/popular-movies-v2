package com.jshvarts.popularmovies.ui;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.AppCompatImageView;
import android.test.suitebuilder.annotation.LargeTest;

import com.jshvarts.popularmovies.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.instanceOf;

/**
 * Instrumentation test for the MovieListActivity and its fragments.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class MovieListActivityInstrumentationTest {
    @Rule
    public ActivityTestRule<MovieListActivity> activityTestRule = new ActivityTestRule<>(MovieListActivity.class, true, false);

    @Test
    public void movieGridView_isVisible_onInitialLoad() {
        launchActivity();

        onView(withId(R.id.movie_list_gridview))
                .check(matches(isDisplayed()));
    }

    @Test
    public void movieListItem_isVisible_onInitialLoad() {
        launchActivity();

        onView(withId(R.id.movie_list_item_imageview))
                .check(matches(isDisplayed()));

        onData(is(instanceOf(AppCompatImageView.class)))
                .inAdapterView(withId(R.id.movie_detail_fragment))
                .atPosition(0)
                .onChildView(withId(R.id.movie_list_item_imageview))
                .check(matches(isDisplayed()));
    }

    private void launchActivity() {
        activityTestRule.launchActivity(new Intent());
    }
}
