package com.jshvarts.popularmovies.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

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

        int id = getIntent().getIntExtra(MovieDetailActivity.MOVIE_ID_EXTRA, -1);
        if (id == -1) {
            return;
        }

        EventBus.getDefault().post(new MovieDetailsRequestRoutedEvent(id));
    }
}
