package com.jshvarts.popularmovies.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.google.common.base.Preconditions;
import com.jshvarts.popularmovies.R;
import com.jshvarts.popularmovies.application.MovieReviewsRequestedEvent;

import de.greenrobot.event.EventBus;

/**
 * Movie detail activity. Note: there is no need to add the options menu in this activity
 */
public class MovieReviewListActivity extends AppCompatActivity {

    protected static final String MOVIE_ID_EXTRA = "id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_review_list);

        Preconditions.checkState(getIntent() != null, "valid intent is required");

        int id = getIntent().getIntExtra(MovieReviewListActivity.MOVIE_ID_EXTRA, -1);
        Preconditions.checkState(id != -1, "valid id in intent is required");

        EventBus.getDefault().post(new MovieReviewsRequestedEvent(id));
    }
}
