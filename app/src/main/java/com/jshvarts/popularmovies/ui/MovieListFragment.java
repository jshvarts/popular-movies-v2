package com.jshvarts.popularmovies.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.jshvarts.popularmovies.R;
import com.jshvarts.popularmovies.application.PopularMoviesApplication;
import com.jshvarts.popularmovies.data.Movie;
import com.jshvarts.popularmovies.data.MovieApiClient;
import com.jshvarts.popularmovies.data.MovieResults;
import com.squareup.okhttp.ResponseBody;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Movie list fragment responsible for loading data for grid layout.
 */
public class MovieListFragment extends Fragment {

    private static final String LOG_TAG = MovieListFragment.class.getSimpleName();

    private static final String INSTANCE_STATE_KEY = "movieList";

    private static final String MOVIE_LIST_UNAVAILABLE = "Unable to retrieve movie list. Please try again.";

    @Inject
    protected MovieApiClient movieApiClient;

    @Inject
    protected SharedPreferences sharedPreferences;

    @Bind(R.id.movie_list_gridview)
    protected GridView gridView;

    @BindString(R.string.pref_sort_by_key)
    protected String prefSortByKey;

    private ListAdapter movieListAdapter;

    private List<Movie> movieList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_STATE_KEY)) {
            movieList = savedInstanceState.getParcelableArrayList(INSTANCE_STATE_KEY);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (movieList != null) {
            outState.putParcelableArrayList(INSTANCE_STATE_KEY, new ArrayList<>(movieList));
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_list, container, false);
        ButterKnife.bind(this, rootView);

        // Inject dependencies of this fragment.
        ((PopularMoviesApplication) getActivity().getApplication()).getDaggerComponent().inject(this);

        retrieveMovieList();

        return rootView;
    }

    /**
     * Makes async call via Retrofit and populates the Adapter with results
     */
    private void retrieveMovieList() {

        // use state if available
        if (movieList != null) {
            initializeAdapter(movieList);
            return;
        }

        String sortBy = sharedPreferences.getString((prefSortByKey), getString(R.string.pref_sort_by_most_popular));

        final Call<MovieResults> call = movieApiClient.movies(sortBy);
        call.enqueue(new Callback<MovieResults>() {

            @Override
            public void onResponse(Response<MovieResults> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    MovieResults results = response.body();
                    if (results != null
                            && results.getMovies() != null
                            && !results.getMovies().isEmpty()) {
                        initializeAdapter(results.getMovies());
                    } else {
                        Log.e(LOG_TAG, "empty movie list returned.");
                        reportSystemError();
                    }
                } else {
                    ResponseBody errorBody = response.errorBody();
                    Log.e(LOG_TAG, "failed to get movie list. response code: "
                            + response.code() + ", errorBody: " + errorBody);
                    reportSystemError();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e(LOG_TAG, "failed to get movie list. " + t.getMessage());
                reportSystemError();
            }
        });
    }

    /**
     * Parses movie list response and assigns the result to the adapter
     * @param movieList
     */
    protected void initializeAdapter(List<Movie> movieList) {

        // save state
        this.movieList = movieList;

        movieListAdapter = new ImageAdapter(getActivity(), movieList);
        gridView.setAdapter(movieListAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(LOG_TAG, "item clicked: " + id);
                Intent detailIntent = new Intent(getActivity(), MovieDetailActivity.class);
                detailIntent.putExtra(MovieDetailActivity.MOVIE_ID_EXTRA, String.valueOf(id));
                startActivity(detailIntent);
            }
        });
    }

    private void reportSystemError() {
        Toast.makeText(getActivity(), MOVIE_LIST_UNAVAILABLE, Toast.LENGTH_SHORT).show();
    }
}
