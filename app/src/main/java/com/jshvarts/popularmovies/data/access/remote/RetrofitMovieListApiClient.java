package com.jshvarts.popularmovies.data.access.remote;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jshvarts.popularmovies.data.model.MovieResults;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;

import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

/**
 * Retrofit implementation of the Movie Api Client.
 */
public class RetrofitMovieListApiClient implements MovieListApiClient {

    private OkHttpClient client;
    private Retrofit retrofitInstance;

    public RetrofitMovieListApiClient() {
        client = new OkHttpClient();

        // Add query param interceptors
        client.interceptors().add(new QueryParamApiKeyInterceptor());
        client.interceptors().add(new QueryParamMinVoteCountInterceptor());

        // Add logging interceptor
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
        client.interceptors().add(loggingInterceptor);

        // Add stetho debug interceptor
        // client.networkInterceptors().add(new StethoInterceptor());

        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

        retrofitInstance = new Retrofit.Builder()
                .baseUrl(MovieDbConstants.BASE_API_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build();
    }

    @Override
    public Call<MovieResults> movies(String sortBy) {
        return retrofitInstance.create(MovieListApiClient.class).movies(sortBy);
    }
}
