package com.jshvarts.popularmovies.application;

import android.content.Context;
import android.text.TextUtils;

import com.google.common.base.Preconditions;
import com.jshvarts.popularmovies.R;

import javax.inject.Singleton;

/**
 * Utils that contains re-usable helper methods.
 */
@Singleton
public class ImageUtils {

    private static final String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/w";

    private Context context;

    public ImageUtils(Context context) {
        this.context = context;
    }

    public String getImageUrl(String posterPath) {
        Preconditions.checkArgument(!TextUtils.isEmpty(posterPath), "posterPath is required");
        StringBuilder imageUrlStringBuilder = new StringBuilder(BASE_IMAGE_URL);
        imageUrlStringBuilder.append(context.getResources().getInteger(R.integer.image_size));
        imageUrlStringBuilder.append(posterPath);
        return imageUrlStringBuilder.toString();
    }
}
