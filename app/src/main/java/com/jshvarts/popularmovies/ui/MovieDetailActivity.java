package com.jshvarts.popularmovies.ui;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.jshvarts.popularmovies.R;
import com.jshvarts.popularmovies.application.MovieDetailsAfterOrientationChangeRequestedEvent;
import com.jshvarts.popularmovies.application.MovieDetailsRequestRoutedEvent;

import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

/**
 * Movie detail activity. Note: there is no need to add the options menu in this activity
 */
public class MovieDetailActivity extends AppCompatActivity {

    private int lastMovieIdViewed;

    public static final String MOVIE_ID_EXTRA = "id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        int id = getIntent().getIntExtra(MOVIE_ID_EXTRA, -1);
        if (id == -1) {
            return;
        }
        lastMovieIdViewed = id;

        EventBus.getDefault().post(new MovieDetailsRequestRoutedEvent(id));
    }

    @Override
    protected void onDestroy() {
        ButterKnife.unbind(this);
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        boolean isDualPane = getResources().getBoolean(R.bool.dual_pane);

        if (!isDualPane) {
            return;
        }

        if (lastMovieIdViewed != 0) {
            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                EventBus.getDefault().postSticky(new MovieDetailsAfterOrientationChangeRequestedEvent(lastMovieIdViewed));
                finish();
            }
        }
    }
}
