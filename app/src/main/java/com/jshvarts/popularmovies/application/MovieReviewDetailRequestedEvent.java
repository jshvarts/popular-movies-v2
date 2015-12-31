package com.jshvarts.popularmovies.application;

import android.text.TextUtils;

import com.google.common.base.Preconditions;

/**
 * Movie review detail requested event for event bus.
 */
public class MovieReviewDetailRequestedEvent {
    private String id;

    public MovieReviewDetailRequestedEvent(String id) {
        Preconditions.checkArgument(!TextUtils.isEmpty(id), "id is required");
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
