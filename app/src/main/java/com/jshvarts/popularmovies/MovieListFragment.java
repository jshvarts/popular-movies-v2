package com.jshvarts.popularmovies;

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

    @Inject
    protected MovieApiClient movieApiClient;

    @Inject
    protected SharedPreferences sharedPreferences;

    @Bind(R.id.movie_list_gridview)
    protected GridView gridView;

    @BindString(R.string.pref_sort_by_key)
    protected String prefSortByKey;

    private ListAdapter movieListAdapter;

    private static final String LOG_TAG = MovieListFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_list, container, false);
        ButterKnife.bind(this, rootView);

        // Inject dependencies of this fragment.
        ((PopularMoviesApplication) getActivity().getApplication()).getDaggerComponent().inject(this);

        populateMovieList();

        return rootView;
    }

    private void populateMovieList() {
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
                    }
                } else {
                    ResponseBody errorBody = response.errorBody();
                    Log.e(LOG_TAG, "failed to get to get movie list. response code: "
                            + response.code() + ", errorBody: " + errorBody);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e(LOG_TAG, "failed to get to get movie list.", t);
            }
        });
    }

    /**
     * Parses movie list response and assigns the result to the adapter
     * @param movieList
     */
    protected void initializeAdapter(List<Movie> movieList) {
        movieListAdapter = new ImageAdapter(getActivity(), movieList);
        gridView.setAdapter(movieListAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(LOG_TAG, "item clicked: " + id);
                Intent detailIntent = new Intent(getActivity().getBaseContext(), MovieDetailActivity.class);
                detailIntent.putExtra(MovieDetailActivity.MOVIE_ID_EXTRA, String.valueOf(id));
                getActivity().startActivity(detailIntent);
            }
        });
    }
}
