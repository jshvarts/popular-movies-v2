package com.jshvarts.popularmovies;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListAdapter;

import com.jshvarts.popularmovies.data.Movie;
import com.jshvarts.popularmovies.data.MovieApiClient;
import com.jshvarts.popularmovies.data.MovieResults;
import com.jshvarts.popularmovies.data.RetrofitMovieApiClient;
import com.squareup.okhttp.ResponseBody;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Movie list fragment responsible for loading data for grid layout.
 */
public class MovieListFragment extends Fragment {

    @Bind(R.id.movie_list_gridview)
    protected GridView gridView;

    private ListAdapter movieListAdapter;

    private static final String LOG_TAG = MovieListFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_list, container, false);
        ButterKnife.bind(this, rootView);

        populateMovieList();

        return rootView;
    }

    private void populateMovieList() {
        MovieApiClient movieApiClient = new RetrofitMovieApiClient();
        //TODO read shared prefs to get sortBy
        final String sortBy = "popularity.desc";
        final Call<MovieResults> call = movieApiClient.movies(sortBy, BuildConfig.THE_MOVIE_DB_API_KEY);
        call.enqueue(new Callback<MovieResults>() {

            @Override
            public void onResponse(Response<MovieResults> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    MovieResults results = response.body();
                    if (results != null && !results.getMovies().isEmpty()) {
                        List<String> posterPathList = new ArrayList<>();
                        for (Movie movie : results.getMovies()) {
                            posterPathList.add(movie.getPosterPath());
                        }
                        movieListAdapter = new ImageAdapter(getActivity(), posterPathList);
                        gridView.setAdapter(movieListAdapter);
                    }
                } else {
                    ResponseBody errorBody = response.errorBody();
                    Log.e(LOG_TAG, "failed to get to get movie list. response code: "
                            + response.code() +", errorBody: " + errorBody);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e(LOG_TAG, "failed to get to get movie list.", t);
            }
        });
    }
}
