package com.jshvarts.popularmovies.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;

import com.google.common.base.Preconditions;
import com.jshvarts.popularmovies.R;
import com.jshvarts.popularmovies.application.MovieDetailsRequestedEvent;
import com.jshvarts.popularmovies.application.MovieReviewDetailRequestedEvent;
import com.jshvarts.popularmovies.application.MovieReviewsRequestedEvent;

import de.greenrobot.event.EventBus;

/**
 * Movie detail activity. Note: there is no need to add the options menu in this activity
 */
public class MovieReviewDetailActivity extends AppCompatActivity {

    protected static final String REVIEW_ID_EXTRA = "id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_review_detail);

        Preconditions.checkState(getIntent() != null, "valid intent is required");

        String id = getIntent().getStringExtra(REVIEW_ID_EXTRA);
        Preconditions.checkState(!TextUtils.isEmpty(id), "valid id in intent is required");

        EventBus.getDefault().post(new MovieReviewDetailRequestedEvent(id));
    }
}
