package com.jshvarts.popularmovies.application;

import com.google.common.base.Preconditions;

/**
 * Movie detail requested event for event bus.
 */
public class MovieDetailsAfterOrientationChangeRequestedEvent {
    private int id;

    public MovieDetailsAfterOrientationChangeRequestedEvent(int id) {
        Preconditions.checkArgument(id != 0, "id is required");
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
