package com.jshvarts.popularmovies.data;

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

    @Override
    public Call<MovieResults> movies(@Query("sort_by") String sortBy, @Query("api_key") String apiKey) {

        OkHttpClient client = new OkHttpClient();
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        client.interceptors().add(interceptor);

        // example url: http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=4f2e672ff19795d0d3b2b7e676b356ea

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        MovieApiClient movieApiClient = retrofit.create(MovieApiClient.class);

        return movieApiClient.movies(sortBy, apiKey);
    }
}
