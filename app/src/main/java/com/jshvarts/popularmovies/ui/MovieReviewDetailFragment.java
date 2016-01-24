package com.jshvarts.popularmovies.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jshvarts.popularmovies.R;
import com.jshvarts.popularmovies.application.MovieReviewDetailRequestedEvent;
import com.jshvarts.popularmovies.application.PopularMoviesApplication;
import com.jshvarts.popularmovies.data.access.remote.MovieDetailApiClient;
import com.jshvarts.popularmovies.data.model.MovieReview;
import com.squareup.leakcanary.RefWatcher;
import com.squareup.okhttp.ResponseBody;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Movie detail fragment responsible for lookup extra movie attributes.
 */
public class MovieReviewDetailFragment extends Fragment {

    private static final String LOG_TAG = MovieReviewDetailFragment.class.getSimpleName();

    private static final String INSTANCE_STATE_KEY = "movieReviewDetails";

    private static final String MOVIE_DETAILS_UNAVAILABLE = "Unable to retrieve movie review details. Please try again.";

    @Inject
    protected MovieDetailApiClient movieDetailApiClient;

    @Bind(R.id.progress_bar)
    protected ProgressBar progressBar;

    @Bind(R.id.review_author)
    protected TextView reviewAuthor;

    @Bind(R.id.review_content)
    protected TextView reviewContent;

    protected MovieReview movieReview;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(INSTANCE_STATE_KEY)) {
                movieReview = savedInstanceState.getParcelable(INSTANCE_STATE_KEY);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(INSTANCE_STATE_KEY, movieReview);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_review_detail, container, false);
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

    public void onEventMainThread(MovieReviewDetailRequestedEvent event) {
        Log.d(LOG_TAG, "review id requested: " + event.getId());
        retrieveMovieReview(event.getId());
    }

    /**
     * Checks to see if the movie details were preserved as part of instance state.
     * If so, uses it to populate the view. Otherwise, makes an async call via Retrofit to get the movie details

     * @param id
     */
    private void retrieveMovieReview(final String id) {

        // use state if available
        if (movieReview != null) {
            initializeMovieReviewUI(movieReview);
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        final Call<MovieReview> call = movieDetailApiClient.review(id);
        call.enqueue(new Callback<MovieReview>() {

            @Override
            public void onResponse(Response<MovieReview> response, Retrofit retrofit) {

                progressBar.setVisibility(View.GONE);

                if (response.isSuccess()) {
                    MovieReview movieReview = response.body();
                    if (movieReview != null) {
                        initializeMovieReviewUI(movieReview);
                    } else {
                        Log.e(LOG_TAG, "empty movie review returned.");
                        reportSystemError();
                    }
                } else {
                    ResponseBody errorBody = response.errorBody();
                    Log.e(LOG_TAG, "failed to get movie review. response code: "
                            + response.code() + ", errorBody: " + errorBody);
                    reportSystemError();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                progressBar.setVisibility(View.GONE);

                Log.e(LOG_TAG, "failed to get movie review. " + t.getMessage());
                reportSystemError();
            }
        });
    }

    private void initializeMovieReviewUI(MovieReview movieReview) {

        // save state
        this.movieReview = movieReview;

        reviewAuthor.setText(movieReview.getAuthor());
        reviewContent.setText(movieReview.getContent());
    }

    private void reportSystemError() {
        Toast.makeText(getActivity(), MOVIE_DETAILS_UNAVAILABLE, Toast.LENGTH_SHORT).show();
    }
}
