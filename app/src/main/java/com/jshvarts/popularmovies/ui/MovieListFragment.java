package com.jshvarts.popularmovies.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.jshvarts.popularmovies.R;
import com.jshvarts.popularmovies.application.MovieDetailsRequestedEvent;
import com.jshvarts.popularmovies.application.PopularMoviesApplication;
import com.jshvarts.popularmovies.application.SharedPrefUpdateEvent;
import com.jshvarts.popularmovies.data.model.Movie;
import com.jshvarts.popularmovies.data.access.remote.MovieListApiClient;
import com.jshvarts.popularmovies.data.model.MovieResults;
import com.squareup.leakcanary.RefWatcher;
import com.squareup.okhttp.ResponseBody;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.BindBool;
import butterknife.BindString;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

import static com.jshvarts.popularmovies.favorites.FavoritesProviderConstants.CONTENT_URI;
import static com.jshvarts.popularmovies.favorites.FavoritesProviderConstants.COLUMN_MOVIE_ID;
import static com.jshvarts.popularmovies.favorites.FavoritesProviderConstants.COLUMN_MOVIE_TITLE;
import static com.jshvarts.popularmovies.favorites.FavoritesProviderConstants.COLUMN_POSTER_PATH;

/**
 * Movie list fragment responsible for loading data for grid layout.
 */
public class MovieListFragment extends Fragment {

    private static final String LOG_TAG = MovieListFragment.class.getSimpleName();

    private static final String INSTANCE_STATE_KEY = "movieList";

    private static final String MOVIE_LIST_UNAVAILABLE = "Unable to retrieve movie list. Please try again.";

    private static final String NO_FAVORITES_YET = "No favorites set up yet. Please browse movies by other sort criteria and set some as favorites.";

    @Inject
    protected MovieListApiClient movieListApiClient;

    @Inject
    protected SharedPreferences sharedPreferences;

    @Bind(R.id.movie_list_gridview)
    protected GridView gridView;

    @Bind(R.id.progress_bar)
    protected ProgressBar progressBar;

    @BindString(R.string.pref_sort_by_key)
    protected String prefSortByKey;

    @BindString(R.string.pref_sort_by_favorites)
    protected String prefSortByFavorites;

    @BindBool(R.bool.dual_pane)
    protected boolean isDualPane;

    private List<Movie> movieList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
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

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        retrieveMovieList();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDetach() {
        EventBus.getDefault().unregister(this);
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = PopularMoviesApplication.getRefWatcher(getActivity());
        refWatcher.watch(this);
        ButterKnife.unbind(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        retrieveMovieList();
    }

    public void onEventMainThread(SharedPrefUpdateEvent event) {
        if (event.getPrefKey().equals(getString(R.string.pref_sort_by_key))) {
            Log.d(LOG_TAG, "shared pref update event received: " + event.getPrefKey());
            movieList = null;
            retrieveMovieList();
        }
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

        if (sortBy.equals(prefSortByFavorites)) {
            String[] projection = new String[] {COLUMN_MOVIE_ID, COLUMN_MOVIE_TITLE, COLUMN_POSTER_PATH};

            Cursor c = getActivity().getContentResolver().query(CONTENT_URI, projection, null, null, null);
            if (c.getCount() == 0) {
                c.close();
                Log.d(getClass().getSimpleName(), "no favorites for user yet");
                Toast.makeText(getActivity(), NO_FAVORITES_YET, Toast.LENGTH_LONG).show();
                return;
            }

            // TODO can this be converted to SimpleCursorAdapter?
            List<Movie> movieList = new ArrayList<>(c.getCount());
            Movie movie;
            while(c.moveToNext()) {
                movie = new Movie(c.getInt(0), c.getString(2));
                movieList.add(movie);
                initializeAdapter(movieList);
            }
            c.close();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        final Call<MovieResults> call = movieListApiClient.movies(sortBy);
        call.enqueue(new Callback<MovieResults>() {

            @Override
            public void onResponse(Response<MovieResults> response, Retrofit retrofit) {

                progressBar.setVisibility(View.GONE);

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
                progressBar.setVisibility(View.GONE);

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

        ListAdapter movieListAdapter = new ImageAdapter(getActivity(), movieList);
        gridView.setAdapter(movieListAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EventBus.getDefault().post(new MovieDetailsRequestedEvent((int) id));
            }
        });
        requestMovieDetail(movieList.get(0).getId());
    }

    /**
     * Initialize the detail screen with the first movie listed if in the dual pane layout.
     */
    private void requestMovieDetail(int movieId) {
        if (!isDualPane) {
            return;
        }

        EventBus.getDefault().post(new MovieDetailsRequestedEvent(movieId));
    }

    private void reportSystemError() {
        Toast.makeText(getActivity(), MOVIE_LIST_UNAVAILABLE, Toast.LENGTH_LONG).show();
    }
}
