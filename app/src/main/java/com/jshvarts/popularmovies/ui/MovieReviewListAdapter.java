package com.jshvarts.popularmovies.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jshvarts.popularmovies.R;
import com.jshvarts.popularmovies.data.model.MovieReview;

import java.util.List;

/**
 * Created by shvartsy on 12/30/15.
 */
public class MovieReviewListAdapter extends RecyclerView.Adapter<MovieReviewListAdapter.ViewHolder> {

    private List<MovieReview> movieReviewList;

    public MovieReviewListAdapter(List<MovieReview> movieReviewList) {
        this.movieReviewList = movieReviewList;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        final Context context = parent.getContext();

        LayoutInflater inflater = LayoutInflater.from(context);

        View movieReviewView = inflater.inflate(R.layout.movie_review_recycler_view_item, parent, false);

        final ViewHolder viewHolder = new ViewHolder(movieReviewView);

        movieReviewView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent detailIntent = new Intent(context, MovieReviewDetailActivity.class);
                String id = movieReviewList.get(viewHolder.getAdapterPosition()).getId();
                detailIntent.putExtra(MovieReviewDetailActivity.REVIEW_ID_EXTRA, id);
                context.startActivity(detailIntent);
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        MovieReview movieReview = movieReviewList.get(position);
        viewHolder.id = movieReview.getId();
        viewHolder.author.setText(movieReview.getAuthor());
        viewHolder.content.setText(movieReview.getContent());
    }

    @Override
    public int getItemCount() {
        return movieReviewList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        String id;
        TextView author;
        TextView content;

        public ViewHolder(View itemView) {
            super(itemView);

            author = (TextView) itemView.findViewById(R.id.review_author);
            content = (TextView) itemView.findViewById(R.id.review_content);
        }
    }
}
