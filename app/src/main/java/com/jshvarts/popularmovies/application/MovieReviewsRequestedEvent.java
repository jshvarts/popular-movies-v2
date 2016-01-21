package com.jshvarts.popularmovies.application;

import com.google.common.base.Preconditions;

/**
 * Movie reviews requested event for event bus.
 */
public class MovieReviewsRequestedEvent {
    private int id;

    public MovieReviewsRequestedEvent(int id) {
        Preconditions.checkArgument(id != 0, "id is required");
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
