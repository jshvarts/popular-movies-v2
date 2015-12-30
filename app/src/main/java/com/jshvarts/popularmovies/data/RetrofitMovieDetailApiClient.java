package com.jshvarts.popularmovies.data;

import com.facebook.stetho.okhttp.StethoInterceptor;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;

import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.http.Path;

/**
 * Retrofit implementation of the Movie Api Client.
 */
public class RetrofitMovieDetailApiClient implements MovieDetailApiClient {

    private OkHttpClient client;
    private Retrofit retrofitInstance;

    public RetrofitMovieDetailApiClient() {
        client = new OkHttpClient();

        // Add query param interceptor
        client.interceptors().add(new QueryParamApiKeyInterceptor());

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
    public Call<MovieDetails> movie(String id) {
        return retrofitInstance.create(MovieDetailApiClient.class).movie(id);
    }

    @Override
    public Call<MovieReviewCountResults> reviewCount(String id) {
        return retrofitInstance.create(MovieDetailApiClient.class).reviewCount(id);
    }
}
