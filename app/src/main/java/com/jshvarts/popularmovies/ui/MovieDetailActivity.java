package com.jshvarts.popularmovies.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.jshvarts.popularmovies.R;
import com.jshvarts.popularmovies.application.MovieDetailsRequestRoutedEvent;

import de.greenrobot.event.EventBus;

/**
 * Movie detail activity. Note: there is no need to add the options menu in this activity
 */
public class MovieDetailActivity extends AppCompatActivity {

    protected static final String MOVIE_ID_EXTRA = "id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        String id = getIntent().getStringExtra(MovieDetailActivity.MOVIE_ID_EXTRA);
        if (TextUtils.isEmpty(id)) {
            return;
        }

        EventBus.getDefault().post(new MovieDetailsRequestRoutedEvent(id));
    }
}
