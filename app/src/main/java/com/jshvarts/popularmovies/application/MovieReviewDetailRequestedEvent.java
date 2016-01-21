package com.jshvarts.popularmovies.application;

import com.google.common.base.Preconditions;

/**
 * Movie review detail requested event for event bus.
 */
public class MovieReviewDetailRequestedEvent {
    private int id;

    public MovieReviewDetailRequestedEvent(int id) {
        Preconditions.checkArgument(id != 0, "id is required");
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
