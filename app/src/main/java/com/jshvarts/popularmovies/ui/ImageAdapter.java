package com.jshvarts.popularmovies.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.jshvarts.popularmovies.R;
import com.jshvarts.popularmovies.application.PopularMoviesApplication;
import com.jshvarts.popularmovies.application.ImageUtils;
import com.jshvarts.popularmovies.data.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;

import javax.inject.Inject;

/**
 * Custom image adapter used to display the movie list in grid view format.
 */
public class ImageAdapter extends BaseAdapter {

    @Inject
    protected ImageUtils imageUtils;

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

        ViewHolder holder = null;

        if (convertView == null || convertView.getId() == -1) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.movie_list_item, null);

            holder = new ViewHolder();
            holder.imageView = (ImageView) convertView.findViewById(R.id.movie_list_item_imageview);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.imageView.setAdjustViewBounds(true);
        String imageUrl = imageUtils.getImageUrl((movieList.get(position)).getPosterPath());
        Picasso.with(context).load(imageUrl).into(holder.imageView);

        return convertView;
    }

    private class ViewHolder {
        private ImageView imageView;
    }
}
