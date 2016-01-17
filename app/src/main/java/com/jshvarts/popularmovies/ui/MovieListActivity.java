package com.jshvarts.popularmovies.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.jshvarts.popularmovies.R;
import com.jshvarts.popularmovies.application.MovieDetailsRequestRoutedEvent;
import com.jshvarts.popularmovies.application.MovieDetailsRequestedEvent;

import de.greenrobot.event.EventBus;

/**
 * Serves as entry point for the application
 */
public class MovieListActivity extends AppCompatActivity {

    private static final String LOG_TAG = MovieListActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);
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
     * Route detail item request. If the detail fragment is not part of the list activity,
     * send an intent to detail activity. otherwise send out a new event to display the item.
     *
     * @param event
     */
    public void onEventMainThread(MovieDetailsRequestedEvent event) {
        MovieDetailFragment detailFragment = (MovieDetailFragment) getSupportFragmentManager().
                findFragmentById(R.id.movie_detail_fragment);
        Log.d(LOG_TAG, "content id requested: " + event.getId());
        if (detailFragment == null || !detailFragment.isInLayout()) {
            Intent detailIntent = new Intent(this, MovieDetailActivity.class);
            detailIntent.putExtra(MovieDetailActivity.MOVIE_ID_EXTRA, String.valueOf(event.getId()));
            startActivity(detailIntent);
        } else {
            EventBus.getDefault().post(new MovieDetailsRequestRoutedEvent(event.getId()));
        }
    }
}
