package com.jshvarts.popularmovies.data;

import com.facebook.stetho.okhttp.StethoInterceptor;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jshvarts.popularmovies.BuildConfig;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;

import java.io.IOException;

import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Retrofit implementation of the Movie Api Client.
 */
public class RetrofitMovieApiClient implements MovieApiClient {

    private final String BASE_API_URL = "http://api.themoviedb.org";
    private final String MIN_VOTE_COUNT_PARAM = "vote_count.gte";
    private final String MIN_VOTE_COUNT_PARAM_VALUE = "1000";
    private final String API_KEY_PARAM = "api_key";

    private OkHttpClient client;
    private MovieApiClient movieApiClient;

    public RetrofitMovieApiClient() {
        client = new OkHttpClient();

        // Add query param interceptor
        client.interceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                HttpUrl url = request.httpUrl().newBuilder()
                        .addQueryParameter(MIN_VOTE_COUNT_PARAM, MIN_VOTE_COUNT_PARAM_VALUE)
                        .addQueryParameter(API_KEY_PARAM, BuildConfig.THE_MOVIE_DB_API_KEY)
                        .build();
                request = request.newBuilder().url(url).build();
                return chain.proceed(request);
            }
        });

        // Add logging interceptor
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
        client.interceptors().add(loggingInterceptor);

        // Add stetho debug interceptor
        client.networkInterceptors().add(new StethoInterceptor());

        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_API_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build();

        movieApiClient = retrofit.create(MovieApiClient.class);
    }

    @Override
    public Call<MovieResults> movies(String sortBy) {
        return movieApiClient.movies(sortBy);
    }

    @Override
    public Call<MovieDetails> movie(String id) {
        return movieApiClient.movie(id);
    }
}
