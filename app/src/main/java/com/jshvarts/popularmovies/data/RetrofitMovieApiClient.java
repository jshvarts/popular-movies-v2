package com.jshvarts.popularmovies.data;

import com.google.common.base.Preconditions;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;

import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.http.Query;

/**
 * Retrofit implementation of the Movie Api Client.
 */
public class RetrofitMovieApiClient implements MovieApiClient {
    private final String BASE_API_URL = "http://api.themoviedb.org";

    private OkHttpClient client;

    public RetrofitMovieApiClient(OkHttpClient client, HttpLoggingInterceptor interceptor) {
        this.client = client;
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        client.interceptors().add(interceptor);
    }

    @Override
    public Call<MovieResults> movies(@Query("sort_by") String sortBy,
                                     @Query("vote_count.gte") int minVoteCount,
                                     @Query("api_key") String apiKey) {
        Preconditions.checkState(client != null, "OkHttpClient must be instantiated");

        // example urls:
        // http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=4f2e672ff19795d0d3b2b7e676b356ea
        // http://api.themoviedb.org/3/discover/movie?sort_by=vote_average.desc&vote_count.gte=1000&api_key=4f2e672ff19795d0d3b2b7e676b356ea

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        MovieApiClient movieApiClient = retrofit.create(MovieApiClient.class);

        return movieApiClient.movies(sortBy, minVoteCount, apiKey);
    }
}
