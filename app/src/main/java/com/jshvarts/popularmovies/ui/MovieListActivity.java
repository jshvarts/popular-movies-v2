package com.jshvarts.popularmovies.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.jshvarts.popularmovies.R;
import com.jshvarts.popularmovies.application.MovieDetailsAfterOrientationChangeRequestedEvent;
import com.jshvarts.popularmovies.application.MovieDetailsRequestRoutedEvent;
import com.jshvarts.popularmovies.application.MovieDetailsRequestedEvent;

import butterknife.BindBool;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

/**
 * Serves as entry point for the application
 */
public class MovieListActivity extends AppCompatActivity {

    public static final String MOVIE_ID_EXTRA = "id";

    @BindBool(R.bool.dual_pane)
    protected boolean isDualPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);
        ButterKnife.bind(this);

        int id = getIntent().getIntExtra(MOVIE_ID_EXTRA, -1);
        if (id == -1) {
            return;
        }

        EventBus.getDefault().postSticky(new MovieDetailsAfterOrientationChangeRequestedEvent(id));
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        ButterKnife.unbind(this);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Route detail item request. If in dual pane mode, send an intent to detail activity.
     * otherwise send out a new event to display the item.
     *
     * @param event
     */
    public void onEventMainThread(MovieDetailsRequestedEvent event) {
        if (!isDualPane) {
            Intent detailIntent = new Intent(this, MovieDetailActivity.class);
            detailIntent.putExtra(MovieDetailActivity.MOVIE_ID_EXTRA, event.getId());
            startActivity(detailIntent);
        } else {
            EventBus.getDefault().post(new MovieDetailsRequestRoutedEvent(event.getId()));
        }
    }
}
