package com.jshvarts.popularmovies.application;

import android.text.TextUtils;

import com.google.common.base.Preconditions;

/**
 * Movie detail request routed event for event bus.
 */
public class MovieDetailsRequestRoutedEvent {
    private String id;

    public MovieDetailsRequestRoutedEvent(String id) {
        Preconditions.checkArgument(!TextUtils.isEmpty(id), "id is required");
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
