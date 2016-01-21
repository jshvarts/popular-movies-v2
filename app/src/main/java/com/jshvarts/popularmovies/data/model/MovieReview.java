package com.jshvarts.popularmovies.data.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * POJO that represents the movie review.
 */
public class MovieReview implements Parcelable {
    private final int id;

    private final String author;

    private final String content;

    public MovieReview(int id, String author, String content, String url) {
        this.id = id;
        this.author = author;
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    protected MovieReview(Parcel in) {
        id = in.readInt();
        author = in.readString();
        content = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(author);
        dest.writeString(content);
    }

    public static final Creator<MovieReview> CREATOR = new Creator<MovieReview>() {
        public MovieReview createFromParcel(Parcel in) {
            return new MovieReview(in);
        }

        public MovieReview[] newArray(int size) {
            return new MovieReview[size];
        }
    };
}
