package com.jshvarts.popularmovies.application;

import com.google.common.base.Preconditions;

/**
 * Movie detail request routed event for event bus.
 */
public class MovieDetailsRequestRoutedEvent {
    private int id;

    public MovieDetailsRequestRoutedEvent(int id) {
        Preconditions.checkArgument(id != 0, "id is required");
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
