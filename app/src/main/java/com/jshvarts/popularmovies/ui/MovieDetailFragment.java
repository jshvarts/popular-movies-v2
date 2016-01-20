package com.jshvarts.popularmovies.ui;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.base.Preconditions;
import com.jshvarts.popularmovies.R;
import com.jshvarts.popularmovies.application.ImageUtils;
import com.jshvarts.popularmovies.application.MovieDetailsRequestRoutedEvent;
import com.jshvarts.popularmovies.application.PopMoviesDbHelper;
import com.jshvarts.popularmovies.application.PopularMoviesApplication;
import com.jshvarts.popularmovies.data.access.remote.MovieDetailApiClient;
import com.jshvarts.popularmovies.data.model.CompositeMovieDetails;
import com.jshvarts.popularmovies.data.model.MovieDetails;
import com.jshvarts.popularmovies.data.model.MovieReviewCount;
import com.jshvarts.popularmovies.data.model.MovieTrailer;
import com.jshvarts.popularmovies.data.model.MovieTrailerResults;
import com.squareup.leakcanary.RefWatcher;
import com.squareup.okhttp.ResponseBody;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.BindDrawable;
import butterknife.BindString;
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

    private static final String YOUTUBE_PROTOCOL = "vnd.youtube:";

    private static final String YOUTUBE_HTTP_BASE_URL = "http://www.youtube.com/watch?v=";

    private static final String INSTANCE_STATE_MOVIE_DETAIL_KEY = "movieDetails";

    private static final String INSTANCE_STATE_REVIEW_COUNT_KEY = "reviewCount";

    private static final String INSTANCE_STATE_TRAILERS_KEY = "trailers";

    private static final String MOVIE_DETAILS_UNAVAILABLE = "Unable to retrieve movie details. Please try again.";

    private static final String UNABLE_TO_PLAY_TRAILER_MESSAGE =
            "Unable to play trailer. Make sure you have either YouTube or a web browser installed.";

    private static final String MOVIE_RATING_OUT_OF_10 = "/10";

    @Inject
    protected MovieDetailApiClient movieDetailApiClient;

    @Inject
    protected ImageUtils imageUtils;

    @Inject
    protected PopMoviesDbHelper dbHelper;

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

    @Bind(R.id.favorite_button)
    protected Button favoriteButton;

    @Bind(R.id.trailer_heading)
    protected TextView trailerHeadingText;

    @Bind(R.id.trailer_links)
    protected TableLayout trailerLinksTable;

    @BindDrawable(R.drawable.play)
    protected Drawable playDrawable;

    @BindString(R.string.button_text_mark_as_favorite)
    protected String buttonTextMarkAsFavorite;

    @BindString(R.string.button_text_favorite)
    protected String buttonTextFavorite;

    private CompositeMovieDetails compositeMovieDetails;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            compositeMovieDetails = new CompositeMovieDetails();
            if (savedInstanceState.containsKey(INSTANCE_STATE_MOVIE_DETAIL_KEY)) {
                MovieDetails movieDetails = savedInstanceState.getParcelable(INSTANCE_STATE_MOVIE_DETAIL_KEY);
                compositeMovieDetails.setMovieDetails(movieDetails);
            }
            if (savedInstanceState.containsKey(INSTANCE_STATE_REVIEW_COUNT_KEY)) {
                int reviewCount = savedInstanceState.getInt(INSTANCE_STATE_REVIEW_COUNT_KEY);
                compositeMovieDetails.setReviewCount(reviewCount);
            }
            if (savedInstanceState.containsKey(INSTANCE_STATE_TRAILERS_KEY)) {
                List<MovieTrailer> trailers = savedInstanceState.getParcelableArrayList(INSTANCE_STATE_TRAILERS_KEY);
                compositeMovieDetails.setMovieTrailerList(trailers);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (compositeMovieDetails != null) {
            if (compositeMovieDetails.getMovieDetails() != null) {
                outState.putParcelable(INSTANCE_STATE_MOVIE_DETAIL_KEY, compositeMovieDetails.getMovieDetails());
            }
            if (compositeMovieDetails.getReviewCount() != CompositeMovieDetails.REVIEW_COUNT_PENDING_LOOKUP) {
                outState.putInt(INSTANCE_STATE_REVIEW_COUNT_KEY, compositeMovieDetails.getReviewCount());
            }
            if (compositeMovieDetails.getMovieTrailerList() != null) {
                outState.putParcelableArrayList(INSTANCE_STATE_TRAILERS_KEY, new ArrayList<>(compositeMovieDetails.getMovieTrailerList()));
            }
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
        ButterKnife.unbind(this);
    }

    public void onEventMainThread(MovieDetailsRequestRoutedEvent event) {
        retrieveMovie(event.getId());
    }

    @Override
    public void onResume() {
        super.onResume();

        if (compositeMovieDetails != null && compositeMovieDetails.getMovieDetails() != null) {
            retrieveMovie(String.valueOf(compositeMovieDetails.getMovieDetails().getId()));
        }
    }

    @OnClick({R.id.review_count_label, R.id.review_count})
    protected void onReviewsButtonClick() {
        Preconditions.checkNotNull(compositeMovieDetails);
        Preconditions.checkNotNull(compositeMovieDetails.getMovieDetails());
        if (compositeMovieDetails.getReviewCount() <= 0) {
            // no reviews to show
            return;
        }
        Intent moviewReviewsIntent = new Intent(getActivity(), MovieReviewListActivity.class);
        moviewReviewsIntent.putExtra(MovieReviewListActivity.MOVIE_ID_EXTRA,
                String.valueOf(compositeMovieDetails.getMovieDetails().getId()));
        startActivity(moviewReviewsIntent);
    }

    @OnClick(R.id.favorite_button)
    protected void onMarkAsFavoriteButtonClick() {
        Preconditions.checkNotNull(compositeMovieDetails);
        Preconditions.checkNotNull(compositeMovieDetails.getMovieDetails());

        ContentValues movieFavoritelValues = new ContentValues();
        movieFavoritelValues.put(PopMoviesDbHelper.MOVIE_ID, compositeMovieDetails.getMovieDetails().getId());
        movieFavoritelValues.put(PopMoviesDbHelper.MOVIE_TITLE, compositeMovieDetails.getMovieDetails().getOriginalTitle());
        movieFavoritelValues.put(PopMoviesDbHelper.POSTER_PATH, compositeMovieDetails.getMovieDetails().getPosterPath());
        dbHelper.getWritableDatabase().insert(PopMoviesDbHelper.TABLE_FAVORITES, null, movieFavoritelValues);

        toggleFavoriteButton(true);

        Cursor c = dbHelper.getReadableDatabase().rawQuery(PopMoviesDbHelper.QUERY_FAVORITES, null);
        while(c.moveToNext()) {
            Log.d(getClass().getSimpleName(), "id found: " + c.getInt(0));
            Log.d(getClass().getSimpleName(), "title found: " + c.getString(1));
            Log.d(getClass().getSimpleName(), "poster_path found: " + c.getString(2));
        }
        c.close();
    }

    /**
     * Checks to see if the movie details were preserved as part of instance state.
     * If so, uses it to populate the view. Otherwise, makes an async call via Retrofit to get the movie details

     * @param id
     */
    private void retrieveMovie(final String id) {

        if (!isNewMovieRequest(id)) {
            // use state if available
            if (compositeMovieDetails != null) {
                initializeMovieDetailsUI(compositeMovieDetails.getMovieDetails());
                initializeReviewCountUI(compositeMovieDetails.getReviewCount());
                initializeTrailersUI(compositeMovieDetails.getMovieTrailerList());
                return;
            }
        }
        compositeMovieDetails = new CompositeMovieDetails();

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
                        // movie details available. now look up review and trailers data
                        retrieveReviewCount(id);
                        retrieveTrailers(id);
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

    private boolean isNewMovieRequest(String id) {
        Preconditions.checkArgument(TextUtils.isDigitsOnly(id), "invalid id passed in");
        if (compositeMovieDetails == null) {
            return true;
        }
        if (compositeMovieDetails.getMovieDetails() != null
                && compositeMovieDetails.getMovieDetails().getId() != Integer.parseInt(id)) {
            return true;
        }
        return false;
    }

    /**
     * Checks to see if the movie review count was preserved as part of instance state.
     * If so, uses it to populate the view. Otherwise, makes an async call via Retrofit to get the review count

     * @param id
     */
    private void retrieveReviewCount(String id) {
        Preconditions.checkNotNull(compositeMovieDetails);
        Preconditions.checkArgument(id != null);
        // use state if available
        if (CompositeMovieDetails.REVIEW_COUNT_PENDING_LOOKUP != compositeMovieDetails.getReviewCount()) {
            initializeReviewCountUI(compositeMovieDetails.getReviewCount());
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

                    compositeMovieDetails.setReviewCount(CompositeMovieDetails.REVIEW_COUNT_UNAVAILABLE);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e(LOG_TAG, "failed to get movie review count. " + t.getMessage());

                compositeMovieDetails.setReviewCount(CompositeMovieDetails.REVIEW_COUNT_UNAVAILABLE);
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
        Preconditions.checkNotNull(compositeMovieDetails);
        Preconditions.checkNotNull(movieDetails);

        // save state
        compositeMovieDetails.setMovieDetails(movieDetails);

        Log.d(getClass().getSimpleName(), "HERE " + movieDetails.getId());

        String imageUrl = imageUtils.getImageUrl(movieDetails.getPosterPath());
        Picasso.with(getActivity()).load(imageUrl).into(posterImage);

        originalTitle.setText(movieDetails.getOriginalTitle());
        originalTitle.setVisibility(View.VISIBLE);
        overview.setText(movieDetails.getOverview());
        voteAverage.setText(String.valueOf(movieDetails.getVoteAverage()) + MOVIE_RATING_OUT_OF_10);

        String releaseYear = getReleaseYear(movieDetails.getReleaseDate());
        if (!TextUtils.isEmpty(releaseYear)) {
            releaseDate.setText(releaseYear);
        }

        if (isMovieFavorite(movieDetails.getId())) {
            toggleFavoriteButton(true);
        } else {
            toggleFavoriteButton(false);
        }
        favoriteButton.setVisibility(View.VISIBLE);
    }

    private void initializeReviewCountUI(int reviewCount) {
        Preconditions.checkNotNull(compositeMovieDetails);

        // save state
        compositeMovieDetails.setReviewCount(reviewCount);

        reviewCountLabel.setVisibility(View.VISIBLE);
        StringBuilder reviewCountWithBraces = new StringBuilder(String.valueOf(reviewCount));
        reviewCountWithBraces.insert(0, "(");
        reviewCountWithBraces.append(")");

        if (reviewCount > 0) {
            SpannableString spannableReviewCount = new SpannableString(reviewCountWithBraces);
            spannableReviewCount.setSpan(new UnderlineSpan(), 0, spannableReviewCount.length(), 0);
            reviewCountText.setText(spannableReviewCount);
        } else {
            reviewCountText.setText(reviewCountWithBraces);
        }
    }

    private boolean isMovieFavorite(int movieId) {
        Cursor c = dbHelper.getReadableDatabase().query(PopMoviesDbHelper.TABLE_FAVORITES,
                new String[]{PopMoviesDbHelper.MOVIE_ID},
                PopMoviesDbHelper.MOVIE_ID + "=?",
                new String[]{String.valueOf(movieId)},
                null,
                null,
                null);

        if (c.getCount() == 0) {
            toggleFavoriteButton(false);
            c.close();
            return false;
        }
        while (c.moveToNext()) {
            Log.d(getClass().getSimpleName(), "favorite id found: " + c.getInt(0));
            toggleFavoriteButton(true);
            if (!c.isLast()) {
                Log.e(getClass().getSimpleName(),
                        "more than one value returned for movie id " + compositeMovieDetails.getMovieDetails().getId());
            }
            c.close();
            return true;
        }
        c.close();
        return false;
    }

    private void toggleFavoriteButton(boolean isFavorite) {
        if (isFavorite) {
            favoriteButton.setText(buttonTextFavorite);
            favoriteButton.setEnabled(false);
        } else {
            favoriteButton.setText(buttonTextMarkAsFavorite);
            favoriteButton.setEnabled(true);
        }
    }
    /**
     * Checks to see if the movie trailers were preserved as part of instance state.
     * If so, uses it to populate the view. Otherwise, makes an async call via Retrofit to get the trailers

     * @param id
     */
    private void retrieveTrailers(String id) {
        Preconditions.checkNotNull(compositeMovieDetails);

        // use state if available
        if (compositeMovieDetails.getMovieTrailerList() != null) {
            initializeTrailersUI(compositeMovieDetails.getMovieTrailerList());
            return;
        }

        final Call<MovieTrailerResults> call = movieDetailApiClient.trailers(id);
        call.enqueue(new Callback<MovieTrailerResults>() {

            @Override
            public void onResponse(Response<MovieTrailerResults> response, Retrofit retrofit) {

                if (response.isSuccess()) {
                    MovieTrailerResults trailerResults = response.body();
                    if (trailerResults != null && trailerResults.getTrailers() != null) {
                        initializeTrailersUI(trailerResults.getTrailers());
                    } else {
                        Log.e(LOG_TAG, "empty trailers results. perhaps this movie has no trailers?");
                    }
                } else {
                    ResponseBody errorBody = response.errorBody();
                    Log.e(LOG_TAG, "failed to get movie trailers. response code: "
                            + response.code() + ", errorBody: " + errorBody);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e(LOG_TAG, "failed to get movie trailers. " + t.getMessage());
            }
        });
    }

    private void initializeTrailersUI(List<MovieTrailer> trailers) {
        Preconditions.checkNotNull(compositeMovieDetails);
        Preconditions.checkNotNull(trailers);

        // save state
        compositeMovieDetails.setMovieTrailerList(trailers);

        if (trailers.isEmpty()) {
            Log.d(LOG_TAG, "no trailers available for movie with id " + compositeMovieDetails.getMovieDetails().getId());
            return;
        }

        trailerHeadingText.setVisibility(View.VISIBLE);

        trailerLinksTable.removeAllViews();

        LinearLayout.LayoutParams rowLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rowLayoutParams.setMargins(0, 20, 0, 20);

        TableRow tr;
        ImageButton playButton;
        TextView trailerName;
        for (final MovieTrailer trailer : trailers) {

            View.OnClickListener trailerOnClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(getActivity(), "play trailer " + trailer.getKey(), Toast.LENGTH_SHORT).show();
                    playTrailer(trailer.getKey());
                }
            };

            tr = new TableRow(getActivity());
            tr.setLayoutParams(rowLayoutParams);
            tr.setGravity(Gravity.CENTER_VERTICAL);

            playButton = new ImageButton(getActivity());
            playButton.setImageDrawable(playDrawable);
            playButton.setOnClickListener(trailerOnClickListener);
            tr.addView(playButton);

            trailerName = new TextView(getActivity());
            trailerName.setText(trailer.getName());
            trailerName.setGravity(Gravity.CENTER_VERTICAL);
            trailerName.setPadding(20, 0, 0, 0);
            trailerName.setOnClickListener(trailerOnClickListener);

            tr.addView(trailerName);
            trailerLinksTable.addView(tr);
        }
    }

    private void initializeCompositeMovieDetailsUI(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            return;
        }
        compositeMovieDetails = new CompositeMovieDetails();
        if (savedInstanceState.containsKey(INSTANCE_STATE_MOVIE_DETAIL_KEY)) {
            MovieDetails movieDetails = savedInstanceState.getParcelable(INSTANCE_STATE_MOVIE_DETAIL_KEY);
            compositeMovieDetails.setMovieDetails(movieDetails);
        }
        if (savedInstanceState.containsKey(INSTANCE_STATE_REVIEW_COUNT_KEY)) {
            int reviewCount = savedInstanceState.getInt(INSTANCE_STATE_REVIEW_COUNT_KEY);
            compositeMovieDetails.setReviewCount(reviewCount);
        }
        if (savedInstanceState.containsKey(INSTANCE_STATE_TRAILERS_KEY)) {
            List<MovieTrailer> trailers = savedInstanceState.getParcelableArrayList(INSTANCE_STATE_TRAILERS_KEY);
            compositeMovieDetails.setMovieTrailerList(trailers);
        }
    }

    /**
     * Attempts playing a video in YouTube or web browser.
     * Handles gracefully if unable to handle the intent.
     *
     * @param key
     */
    private void playTrailer(String key){
        Preconditions.checkArgument(!TextUtils.isEmpty(key));

        Intent intent;
        try {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(YOUTUBE_PROTOCOL + key));
            startActivity(intent);
        } catch (ActivityNotFoundException youtubeAppNotFoundException) {
            try {
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(YOUTUBE_HTTP_BASE_URL + key));
                startActivity(intent);
            } catch (ActivityNotFoundException webBrowserAppNotFoundException) {
                Toast.makeText(getActivity(), UNABLE_TO_PLAY_TRAILER_MESSAGE, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void reportSystemError() {
        Toast.makeText(getActivity(), MOVIE_DETAILS_UNAVAILABLE, Toast.LENGTH_LONG).show();
    }
}
