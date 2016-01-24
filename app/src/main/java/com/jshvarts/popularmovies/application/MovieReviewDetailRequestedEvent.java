package com.jshvarts.popularmovies.application;

import com.google.common.base.Preconditions;

/**
 * Movie review detail requested event for event bus.
 */
public class MovieReviewDetailRequestedEvent {
    private String id;

    public MovieReviewDetailRequestedEvent(String id) {
        Preconditions.checkArgument(id != null, "id is required");
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
