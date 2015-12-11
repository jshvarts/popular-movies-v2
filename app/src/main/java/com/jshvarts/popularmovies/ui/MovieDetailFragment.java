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

    private static final String LOG_TAG = MovieDetailFragment.class.getSimpleName();

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
     * Makes async call via Retrofit to retrieve movie details
     * @param id
     */
    private void retrieveMovie(String id) {

        final Call<MovieDetails> call = movieApiClient.movie(id);
        call.enqueue(new Callback<MovieDetails>() {

            @Override
            public void onResponse(Response<MovieDetails> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    MovieDetails movie = response.body();
                    if (movie != null) {
                        String imageUrl = imageUtils.getImageUrl(movie.getPosterPath());
                        Picasso.with(getActivity()).load(imageUrl).into(posterImage);

                        originalTitle.setText(movie.getOriginalTitle());
                        overview.setText(movie.getOverview());
                        voteAverage.setText(String.valueOf(movie.getVoteAverage()) + "/10");

                        String releaseYear = getReleaseYear(movie.getReleaseDate());
                        if (!TextUtils.isEmpty(releaseYear)) {
                            releaseDate.setText(releaseYear);
                        }
                    } else {
                        Log.e(LOG_TAG, "empty movie details returned.");
                    }
                } else {
                    ResponseBody errorBody = response.errorBody();
                    Log.e(LOG_TAG, "failed to get movie details. response code: "
                            + response.code() + ", errorBody: " + errorBody);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e(LOG_TAG, "failed to get movie details. " + t.getMessage());
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
}
