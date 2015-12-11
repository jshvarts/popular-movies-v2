package com.jshvarts.popularmovies.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.base.Preconditions;
import com.jshvarts.popularmovies.R;
import com.jshvarts.popularmovies.application.ImageUtils;
import com.jshvarts.popularmovies.application.PopularMoviesApplication;
import com.jshvarts.popularmovies.data.MovieApiClient;
import com.jshvarts.popularmovies.data.MovieDetails;
import com.squareup.okhttp.ResponseBody;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Movie detail fragment responsible for lookup extra movie attributes.
 */
public class MovieDetailFragment extends Fragment implements MovieDetailActivity.OnContentDetailRequestedListener {

    private static final String LOG_TAG = MovieDetailFragment.class.getSimpleName();

    private static final String INSTANCE_STATE_KEY = "movieDetails";

    private static final String MOVIE_DETAILS_UNAVAILABLE = "Unable to retrieve movie details. Please try again.";

    private static final String MOVIE_RATING_OUT_OF_10 = "/10";

    @Inject
    protected MovieApiClient movieApiClient;

    @Inject
    protected ImageUtils imageUtils;

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

    protected MovieDetails movieDetails;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_STATE_KEY)) {
            movieDetails = savedInstanceState.getParcelable(INSTANCE_STATE_KEY);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(INSTANCE_STATE_KEY, movieDetails);
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
        ((MovieDetailActivity)getActivity()).setContentDetailRequestedListener(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        ((MovieDetailActivity)getActivity()).removeContentDetailRequestedListener(this);
    }

    @Override
    public void contentRequested(String id) {
        Preconditions.checkArgument(!TextUtils.isEmpty(id), "content id required");
        Log.d(LOG_TAG, "content id requested fragment: " + id);

        retrieveMovie(id);
    }

    /**
     * Checks to see if the movie details were preserved as part of instance state.
     * If so, uses it to populate the view. Otherwise, makes an async call via Retrofit to get the movie details

     * @param id
     */
    private void retrieveMovie(String id) {

        // use state if available
        if (movieDetails != null) {
            initializeMovieDetails(movieDetails);
            return;
        }

        final Call<MovieDetails> call = movieApiClient.movie(id);
        call.enqueue(new Callback<MovieDetails>() {

            @Override
            public void onResponse(Response<MovieDetails> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    MovieDetails movieDetails = response.body();
                    if (movieDetails != null) {
                        initializeMovieDetails(movieDetails);
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
                Log.e(LOG_TAG, "failed to get movie details. " + t.getMessage());
                reportSystemError();
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

    private void initializeMovieDetails(MovieDetails movieDetails) {

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

    private void reportSystemError() {
        Toast.makeText(getActivity(), MOVIE_DETAILS_UNAVAILABLE, Toast.LENGTH_SHORT).show();
    }
}
