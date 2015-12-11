package com.jshvarts.popularmovies.data;

/**
 * POJO that represents the movie detail.
 */
public class MovieDetails extends Movie {

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
}
