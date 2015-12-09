package com.jshvarts.popularmovies;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Custom image adapter used to display the movie list in grid view format.
 */
public class ImageAdapter extends BaseAdapter {

    private static final String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/w185";

    private Context context;
    List<String> posterPathList;

    public ImageAdapter(Context context, List<String> posterPathList) {
        this.context = context;
        this.posterPathList = posterPathList;
    }

    public int getCount() {
        return posterPathList.size();
    }

    public Object getItem(int position) {
        return posterPathList.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(context);
            imageView.setAdjustViewBounds(true);
            Picasso.with(context).load(BASE_IMAGE_URL + posterPathList.get(position)).into(imageView);

        } else {
            imageView = (ImageView) convertView;
        }
        return imageView;
    }
}
