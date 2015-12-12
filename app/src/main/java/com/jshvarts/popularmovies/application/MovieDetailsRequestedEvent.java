package com.jshvarts.popularmovies.application;

import android.text.TextUtils;

import com.google.common.base.Preconditions;

/**
 * Shared pref updated event for event bus.
 */
public class MovieDetailsRequestedEvent {
    private String id;

    public MovieDetailsRequestedEvent(String id) {
        Preconditions.checkArgument(!TextUtils.isEmpty(id), "id is required");
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
