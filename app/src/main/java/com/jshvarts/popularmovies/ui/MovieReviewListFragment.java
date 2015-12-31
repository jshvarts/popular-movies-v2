package com.jshvarts.popularmovies.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.jshvarts.popularmovies.R;
import com.jshvarts.popularmovies.application.MovieReviewsRequestedEvent;
import com.jshvarts.popularmovies.application.PopularMoviesApplication;
import com.jshvarts.popularmovies.data.access.remote.MovieDetailApiClient;
import com.jshvarts.popularmovies.data.model.MovieReview;
import com.jshvarts.popularmovies.data.model.MovieReviewResults;
import com.squareup.leakcanary.RefWatcher;
import com.squareup.okhttp.ResponseBody;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Movie list fragment responsible for loading data for grid layout.
 */
public class MovieReviewListFragment extends Fragment {

    private static final String LOG_TAG = MovieReviewListFragment.class.getSimpleName();

    private static final String INSTANCE_STATE_KEY = "movieReviewList";

    private static final String MOVIE_REVIEW_LIST_UNAVAILABLE = "Unable to retrieve movie reviews. Please try again.";

    @Inject
    protected MovieDetailApiClient movieApiClient;

    @Bind(R.id.progress_bar)
    protected ProgressBar progressBar;

    @Bind(R.id.review_list_recyclerview)
    protected RecyclerView reviewListRecyclerView;

    private RecyclerView.Adapter recyclerViewAdapter;

    private List<MovieReview> movieReviewList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_STATE_KEY)) {
            movieReviewList = savedInstanceState.getParcelableArrayList(INSTANCE_STATE_KEY);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // set this for performance
        reviewListRecyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager recyclerViewLayoutManager = new LinearLayoutManager(getActivity());
        reviewListRecyclerView.setLayoutManager(recyclerViewLayoutManager);

        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST);
        reviewListRecyclerView.addItemDecoration(itemDecoration);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (movieReviewList != null) {
            outState.putParcelableArrayList(INSTANCE_STATE_KEY, new ArrayList<>(movieReviewList));
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_review_list, container, false);
        ButterKnife.bind(this, rootView);

        // Inject dependencies of this fragment.
        ((PopularMoviesApplication) getActivity().getApplication()).getDaggerComponent().inject(this);

        return rootView;
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
    }

    public void onEventMainThread(MovieReviewsRequestedEvent event) {
        retrieveMovieReviewList(event.getId());
    }

    /**
     * Makes async call via Retrofit and populates the Adapter with results
     */
    private void retrieveMovieReviewList(String id) {

        // use state if available
        if (movieReviewList != null) {
            initializeAdapter(movieReviewList);
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        final Call<MovieReviewResults> call = movieApiClient.reviews(id);
        call.enqueue(new Callback<MovieReviewResults>() {

            @Override
            public void onResponse(Response<MovieReviewResults> response, Retrofit retrofit) {

                progressBar.setVisibility(View.GONE);

                if (response.isSuccess()) {
                    MovieReviewResults results = response.body();
                    if (results != null
                            && results.getReviews() != null
                            && !results.getReviews().isEmpty()) {
                        Log.d(LOG_TAG, "number of reviews found: " + results.getReviews().size());
                        initializeAdapter(results.getReviews());
                    } else {
                        Log.e(LOG_TAG, "empty movie review list returned.");
                        reportSystemError();
                    }
                } else {
                    ResponseBody errorBody = response.errorBody();
                    Log.e(LOG_TAG, "failed to get movie review list. response code: "
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

    protected void initializeAdapter(List<MovieReview> movieReviewList) {

        // save state
        this.movieReviewList = movieReviewList;

        recyclerViewAdapter = new MovieReviewListAdapter(movieReviewList);
        reviewListRecyclerView.setAdapter(recyclerViewAdapter);
    }

    private void reportSystemError() {
        Toast.makeText(getActivity(), MOVIE_REVIEW_LIST_UNAVAILABLE, Toast.LENGTH_LONG).show();
    }
}
