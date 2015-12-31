package com.jshvarts.popularmovies.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jshvarts.popularmovies.R;
import com.jshvarts.popularmovies.application.ImageUtils;
import com.jshvarts.popularmovies.application.MovieDetailsRequestedEvent;
import com.jshvarts.popularmovies.application.PopularMoviesApplication;
import com.jshvarts.popularmovies.data.access.remote.MovieDetailApiClient;
import com.jshvarts.popularmovies.data.model.MovieDetails;
import com.jshvarts.popularmovies.data.model.MovieReviewCount;
import com.squareup.leakcanary.RefWatcher;
import com.squareup.okhttp.ResponseBody;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Movie detail fragment responsible for lookup extra movie attributes.
 */
public class MovieDetailFragment extends Fragment {

    private static final String LOG_TAG = MovieDetailFragment.class.getSimpleName();

    private static final String INSTANCE_STATE_MOVIE_DETAIL_KEY = "movieDetails";

    private static final String INSTANCE_STATE_REVIEW_COUNT_KEY = "reviewCount";

    private static final String MOVIE_DETAILS_UNAVAILABLE = "Unable to retrieve movie details. Please try again.";

    private static final String MOVIE_RATING_OUT_OF_10 = "/10";

    private static final int REVIEW_COUNT_PENDING_LOOKUP = -1;

    private static final int REVIEW_COUNT_UNAVAILABLE = 0;

    @Inject
    protected MovieDetailApiClient movieDetailApiClient;

    @Inject
    protected ImageUtils imageUtils;

    @Bind(R.id.progress_bar)
    protected ProgressBar progressBar;

    @Bind(R.id.poster_image)
    protected ImageView posterImage;

    @Bind(R.id.original_title)
    protected TextView originalTitle;

    @Bind(R.id.overview)
    protected TextView overview;

    @Bind(R.id.vote_average)
    protected TextView voteAverage;

    @Bind(R.id.release_date)
    protected TextView releaseDate;

    @Bind(R.id.review_count_label)
    protected TextView reviewCountLabel;

    @Bind(R.id.review_count)
    protected TextView reviewCountText;

    @Bind(R.id.read_reviews_link)
    protected TextView readReviewsLink;

    protected MovieDetails movieDetails;

    protected int reviewCount = REVIEW_COUNT_PENDING_LOOKUP;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(INSTANCE_STATE_MOVIE_DETAIL_KEY)) {
                movieDetails = savedInstanceState.getParcelable(INSTANCE_STATE_MOVIE_DETAIL_KEY);
            }
            if (savedInstanceState.containsKey(INSTANCE_STATE_REVIEW_COUNT_KEY)) {
                reviewCount = savedInstanceState.getInt(INSTANCE_STATE_REVIEW_COUNT_KEY);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(INSTANCE_STATE_MOVIE_DETAIL_KEY, movieDetails);
        if (REVIEW_COUNT_PENDING_LOOKUP != reviewCount) {
            outState.putInt(INSTANCE_STATE_REVIEW_COUNT_KEY, reviewCount);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
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

    public void onEventMainThread(MovieDetailsRequestedEvent event) {
        Log.d(LOG_TAG, "content id requested: " + event.getId());
        retrieveMovie(event.getId());
    }

    @OnClick(R.id.read_reviews_link)
    protected void onReadReviewsLinkClick() {
        Intent moviewReviewsIntent = new Intent(getActivity(), MovieReviewListActivity.class);
        moviewReviewsIntent.putExtra(MovieReviewListActivity.MOVIE_ID_EXTRA, String.valueOf(movieDetails.getId()));
        startActivity(moviewReviewsIntent);
    }

    /**
     * Checks to see if the movie details were preserved as part of instance state.
     * If so, uses it to populate the view. Otherwise, makes an async call via Retrofit to get the movie details

     * @param id
     */
    private void retrieveMovie(final String id) {

        // use state if available
        if (movieDetails != null && REVIEW_COUNT_UNAVAILABLE != reviewCount) {
            initializeMovieDetailsUI(movieDetails);
            initializeReviewCountUI(reviewCount);
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        final Call<MovieDetails> call = movieDetailApiClient.movie(id);
        call.enqueue(new Callback<MovieDetails>() {

            @Override
            public void onResponse(Response<MovieDetails> response, Retrofit retrofit) {

                progressBar.setVisibility(View.GONE);

                if (response.isSuccess()) {
                    MovieDetails movieDetails = response.body();
                    if (movieDetails != null) {
                        initializeMovieDetailsUI(movieDetails);
                        retrieveReviewCount(id);
                    } else {
                        Log.e(LOG_TAG, "empty movie details returned.");
                        reportSystemError();
                    }
                } else {
                    ResponseBody errorBody = response.errorBody();
                    Log.e(LOG_TAG, "failed to get movie details. response code: "
                            + response.code() + ", errorBody: " + errorBody);
                    reportSystemError();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                progressBar.setVisibility(View.GONE);

                Log.e(LOG_TAG, "failed to get movie details. " + t.getMessage());
                reportSystemError();
            }
        });
    }

    /**
     * Checks to see if the movie review count was preserved as part of instance state.
     * If so, uses it to populate the view. Otherwise, makes an async call via Retrofit to get the review count

     * @param id
     */
    private void retrieveReviewCount(String id) {

        // use state if available
        if (REVIEW_COUNT_PENDING_LOOKUP != reviewCount) {
            initializeReviewCountUI(reviewCount);
            return;
        }

        final Call<MovieReviewCount> call = movieDetailApiClient.reviewCount(id);
        call.enqueue(new Callback<MovieReviewCount>() {

            @Override
            public void onResponse(Response<MovieReviewCount> response, Retrofit retrofit) {

                progressBar.setVisibility(View.GONE);

                if (response.isSuccess()) {
                    MovieReviewCount reviewCountResults = response.body();
                    if (reviewCountResults != null) {
                        initializeReviewCountUI(reviewCountResults.getTotalResults());
                    } else {
                        Log.e(LOG_TAG, "empty movie review count.");
                        reportSystemError();
                    }
                } else {
                    ResponseBody errorBody = response.errorBody();
                    Log.e(LOG_TAG, "failed to get movie review count. response code: "
                            + response.code() + ", errorBody: " + errorBody);

                    reviewCount = REVIEW_COUNT_UNAVAILABLE;
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e(LOG_TAG, "failed to get movie review count. " + t.getMessage());

                reviewCount = REVIEW_COUNT_UNAVAILABLE;
            }
        });
    }

    /**
     * Extracts year portion for presentation from the release date
     *
     * @param releaseDate
     * @return
     */
    protected String getReleaseYear(String releaseDate) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date releaseDateAsDate = format.parse(releaseDate);
            Calendar calendarDate = Calendar.getInstance();
            calendarDate.setTime(releaseDateAsDate);
            return String.valueOf(calendarDate.get(Calendar.YEAR));
        } catch (ParseException e) {
            Log.e(LOG_TAG, "error parsing release year");
        }
        return null;
    }

    private void initializeMovieDetailsUI(MovieDetails movieDetails) {

        // save state
        this.movieDetails = movieDetails;

        String imageUrl = imageUtils.getImageUrl(movieDetails.getPosterPath());
        Picasso.with(getActivity()).load(imageUrl).into(posterImage);

        originalTitle.setText(movieDetails.getOriginalTitle());
        overview.setText(movieDetails.getOverview());
        voteAverage.setText(String.valueOf(movieDetails.getVoteAverage()) + MOVIE_RATING_OUT_OF_10);

        String releaseYear = getReleaseYear(movieDetails.getReleaseDate());
        if (!TextUtils.isEmpty(releaseYear)) {
            releaseDate.setText(releaseYear);
        }
    }

    private void initializeReviewCountUI(int reviewCount) {

        // save state
        this.reviewCount = reviewCount;

        // display review count label and value after the value is set
        reviewCountLabel.setVisibility(View.VISIBLE);
        reviewCountText.setText(String.valueOf(reviewCount));
        if (reviewCount != REVIEW_COUNT_UNAVAILABLE) {
            readReviewsLink.setVisibility(View.VISIBLE);
        }
    }

    private void reportSystemError() {
        Toast.makeText(getActivity(), MOVIE_DETAILS_UNAVAILABLE, Toast.LENGTH_SHORT).show();
    }
}
