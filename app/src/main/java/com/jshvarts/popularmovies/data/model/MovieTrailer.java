package com.jshvarts.popularmovies.data.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * POJO that represents the movie summary.
 */
public class MovieTrailer implements Parcelable {

    private final String key;

    private final String name;

    // type can be trailer or teaser
    private final String type;

    public MovieTrailer(String key, String name, String type) {
        this.key = key;
        this.name = name;
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    protected MovieTrailer(Parcel in) {
        key = in.readString();
        name = in.readString();
        type = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(key);
        dest.writeString(name);
        dest.writeString(type);
    }

    public static final Creator<MovieTrailer> CREATOR = new Creator<MovieTrailer>() {
        public MovieTrailer createFromParcel(Parcel in) {
            return new MovieTrailer(in);
        }

        public MovieTrailer[] newArray(int size) {
            return new MovieTrailer[size];
        }
    };
}
