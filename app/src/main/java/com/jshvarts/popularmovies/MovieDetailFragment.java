package com.jshvarts.popularmovies;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.base.Preconditions;
import com.jshvarts.popularmovies.application.PopularMoviesApplication;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Movie detail fragment responsible for lookup extra movie attributes.
 */
public class MovieDetailFragment extends Fragment implements MovieDetailActivity.OnContentDetailRequestedListener {

    private static final String LOG_TAG = MovieDetailFragment.class.getSimpleName();

    @Bind(R.id.movie_detail_id)
    protected TextView movieDetailIdTextView;

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
        Toast.makeText(getActivity(), "content id requested fragment: " + id, Toast.LENGTH_SHORT).show();
    }
}
