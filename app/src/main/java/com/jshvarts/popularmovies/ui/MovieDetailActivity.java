package com.jshvarts.popularmovies.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.common.base.Preconditions;
import com.jshvarts.popularmovies.R;
import com.jshvarts.popularmovies.application.MovieDetailsRequestedEvent;

import de.greenrobot.event.EventBus;

/**
 * Movie detail activity
 */
public class MovieDetailActivity extends AppCompatActivity {

    protected static final String MOVIE_ID_EXTRA = "id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        Preconditions.checkState(getIntent() != null, "valid intent is required");

        String id = getIntent().getStringExtra(MovieDetailActivity.MOVIE_ID_EXTRA);
        Preconditions.checkState(!TextUtils.isEmpty(id), "valid id in intent is required");

        EventBus.getDefault().post(new MovieDetailsRequestedEvent(id));
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
}
