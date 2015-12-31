package com.jshvarts.popularmovies.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.collect.Lists;
import com.jshvarts.popularmovies.R;
import com.jshvarts.popularmovies.data.model.MovieTrailer;

import java.util.List;

/**
 * Created by shvartsy on 12/30/15.
 */
public class MovieTrailerListAdapter extends RecyclerView.Adapter<MovieTrailerListAdapter.ViewHolder> {

    private List<MovieTrailer> movieTrailerList;

    public MovieTrailerListAdapter(List<MovieTrailer> movieTrailerList) {
        this.movieTrailerList = movieTrailerList;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        final Context context = parent.getContext();

        LayoutInflater inflater = LayoutInflater.from(context);

        View movieReviewView = inflater.inflate(R.layout.movie_trailer_recycler_view_item, parent, false);

        final ViewHolder viewHolder = new ViewHolder(movieReviewView);

        movieReviewView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String trailerKey = movieTrailerList.get(viewHolder.getAdapterPosition()).getKey();
                Toast.makeText(context, "launch youtube intent for key " + trailerKey, Toast.LENGTH_LONG).show();
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        MovieTrailer movieTrailer = movieTrailerList.get(position);
        viewHolder.key = movieTrailer.getKey();
        viewHolder.name.setText(movieTrailer.getName());
    }

    @Override
    public int getItemCount() {
        return movieTrailerList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        String key;
        TextView name;

        public ViewHolder(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.trailer_name);
        }
    }
}
