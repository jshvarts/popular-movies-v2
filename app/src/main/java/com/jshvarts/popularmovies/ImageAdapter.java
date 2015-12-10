package com.jshvarts.popularmovies;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.jshvarts.popularmovies.data.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Custom image adapter used to display the movie list in grid view format.
 */
public class ImageAdapter extends BaseAdapter {

    private static final String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/w";

    private Context context;
    private List<Movie> movieList;

    public ImageAdapter(Context context, List<Movie> movieList) {
        this.context = context;
        this.movieList = movieList;
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
            String imageUrl = getImageUrl((Movie) movieList.get(position));
            Picasso.with(context).load(imageUrl).into(imageView);
        } else {
            imageView = (ImageView) convertView;
        }
        return imageView;
    }

    private String getImageUrl(Movie movie) {
        StringBuilder imageUrlStringBuilder = new StringBuilder(BASE_IMAGE_URL);
        imageUrlStringBuilder.append(context.getResources().getInteger(R.integer.image_size));
        imageUrlStringBuilder.append(movie.getPosterPath());
        return imageUrlStringBuilder.toString();
    }
}
