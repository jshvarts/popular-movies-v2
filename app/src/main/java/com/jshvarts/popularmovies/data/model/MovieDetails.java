package com.jshvarts.popularmovies.data.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * POJO that represents the movie detail.
 */
public class MovieDetails extends Movie implements Parcelable {

    private final String originalTitle;

    private final String overview;

    private final double voteAverage;

    private final String releaseDate;

    public MovieDetails(int id, String posterPath, String originalTitle, String overview, double voteAverage, String releaseDate) {
        super(id, posterPath);
        this.originalTitle = originalTitle;
        this.overview = overview;
        this.voteAverage = voteAverage;
        this.releaseDate = releaseDate;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public String getOverview() {
        return overview;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    protected MovieDetails(Parcel in){
        super(in);
        this.originalTitle = in.readString();
        this.overview = in.readString();
        this.voteAverage = in.readDouble();
        this.releaseDate = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(originalTitle);
        dest.writeString(overview);
        dest.writeDouble(voteAverage);
        dest.writeString(releaseDate);
    }

    public static final Parcelable.Creator<MovieDetails> CREATOR = new Parcelable.Creator<MovieDetails>() {
        public MovieDetails createFromParcel(Parcel in) {
            return new MovieDetails(in);
        }

        public MovieDetails[] newArray(int size) {
            return new MovieDetails[size];
        }
    };
}
