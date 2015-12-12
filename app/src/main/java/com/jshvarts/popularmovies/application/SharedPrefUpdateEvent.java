package com.jshvarts.popularmovies.application;

import android.text.TextUtils;

import com.google.common.base.Preconditions;

/**
 * Shared pref updated event for event bus.
 */
public class SharedPrefUpdateEvent {
    private String prefKey = null;

    public SharedPrefUpdateEvent(String prefKey) {
        Preconditions.checkArgument(!TextUtils.isEmpty(prefKey), "prefKey is required");
        this.prefKey = prefKey;
    }

    public String getPrefKey() {
        return prefKey;
    }
}
