package com.jshvarts.popularmovies.application;

import android.text.TextUtils;

import com.google.common.base.Preconditions;

/**
 * Movie reviews requested event for event bus.
 */
public class MovieReviewsRequestedEvent {
    private String id;

    public MovieReviewsRequestedEvent(String id) {
        Preconditions.checkArgument(!TextUtils.isEmpty(id), "id is required");
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
