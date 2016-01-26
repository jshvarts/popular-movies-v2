package com.jshvarts.popularmovies.application;

import com.google.common.base.Preconditions;

/**
 * this event is used to "remember" last movie viewed between single and dual pane modes.
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
