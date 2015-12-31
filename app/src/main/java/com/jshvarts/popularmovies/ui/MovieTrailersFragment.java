package com.jshvarts.popularmovies.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jshvarts.popularmovies.R;
import com.jshvarts.popularmovies.application.MovieDetailsRequestedEvent;
import com.jshvarts.popularmovies.application.PopularMoviesApplication;
import com.jshvarts.popularmovies.data.access.remote.MovieDetailApiClient;
import com.jshvarts.popularmovies.data.model.MovieTrailer;
import com.jshvarts.popularmovies.data.model.MovieTrailerResults;
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
 * Movie detail fragment responsible for lookup extra movie attributes.
 */
public class MovieTrailersFragment extends Fragment {

    private static final String LOG_TAG = MovieTrailersFragment.class.getSimpleName();

    private static final String INSTANCE_STATE_TRAILERS_KEY = "trailers";

    @Inject
    protected MovieDetailApiClient movieDetailApiClient;

    @Bind(R.id.trailer_list_recyclerview)
    protected RecyclerView trailerListRecyclerView;

    private RecyclerView.Adapter trailerListRecyclerViewAdapter;

    private List<MovieTrailer> trailers;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(INSTANCE_STATE_TRAILERS_KEY)) {
                trailers = savedInstanceState.getParcelableArrayList(INSTANCE_STATE_TRAILERS_KEY);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (trailers != null) {
            outState.putParcelableArrayList(INSTANCE_STATE_TRAILERS_KEY, new ArrayList<>(trailers));
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_trailers, container, false);
        ButterKnife.bind(this, rootView);

        // Inject dependencies of this fragment.
        ((PopularMoviesApplication) getActivity().getApplication()).getDaggerComponent().inject(this);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // set this for performance
        trailerListRecyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager recyclerViewLayoutManager = new LinearLayoutManager(getActivity());
        trailerListRecyclerView.setLayoutManager(recyclerViewLayoutManager);

        trailerListRecyclerView.setAdapter(new MovieTrailerListAdapter(new ArrayList<MovieTrailer>()));
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
        retrieveTrailers(event.getId());
    }

    /**
     * Checks to see if the movie trailers were preserved as part of instance state.
     * If so, uses it to populate the view. Otherwise, makes an async call via Retrofit to get the trailers

     * @param id
     */
    private void retrieveTrailers(String id) {

        // use state if available
        if (trailers != null && !trailers.isEmpty()) {
            initializeTrailersUI(trailers);
            return;
        }

        final Call<MovieTrailerResults> call = movieDetailApiClient.trailers(id);
        call.enqueue(new Callback<MovieTrailerResults>() {

            @Override
            public void onResponse(Response<MovieTrailerResults> response, Retrofit retrofit) {

                if (response.isSuccess()) {
                    MovieTrailerResults trailerResults = response.body();
                    if (trailerResults != null
                            && trailerResults.getTrailers() != null
                            && !trailerResults.getTrailers().isEmpty()) {
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

        // save state
        this.trailers = trailers;

        trailerListRecyclerViewAdapter = new MovieTrailerListAdapter(trailers);
        trailerListRecyclerView.setAdapter(trailerListRecyclerViewAdapter);
    }
}
