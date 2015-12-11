package com.jshvarts.popularmovies.ui;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.jshvarts.popularmovies.application.PopularMoviesApplication;
import com.jshvarts.popularmovies.application.Utils;
import com.jshvarts.popularmovies.data.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;

import javax.inject.Inject;

/**
 * Custom image adapter used to display the movie list in grid view format.
 */
public class ImageAdapter extends BaseAdapter {

    @Inject
    protected Utils utils;

    private Context context;
    private List<Movie> movieList;

    public ImageAdapter(Context context, List<Movie> movieList) {
        this.context = context;
        this.movieList = movieList;

        // Inject dependencies of this fragment.
        ((PopularMoviesApplication)context.getApplicationContext()).getDaggerComponent().inject(this);
    }

    public int getCount() {
        return movieList.size();
    }

    public Object getItem(int position) {
        return movieList.get(position);
    }

    public long getItemId(int position) {
        return movieList.get(position).getId();
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize it
            imageView = new ImageView(context);
            imageView.setAdjustViewBounds(true);
            String imageUrl = utils.getImageUrl(((Movie) movieList.get(position)).getPosterPath());
            Picasso.with(context).load(imageUrl).into(imageView);
        } else {
            imageView = (ImageView) convertView;
        }
        return imageView;
    }
}
