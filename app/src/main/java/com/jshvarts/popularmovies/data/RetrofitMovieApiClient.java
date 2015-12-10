package com.jshvarts.popularmovies.data;

import com.google.common.base.Preconditions;
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

    public RetrofitMovieApiClient(OkHttpClient client, HttpLoggingInterceptor interceptor) {
        Preconditions.checkArgument(client != null, "OkHttpClient is required");
        Preconditions.checkArgument(interceptor != null, "HttpLoggingInterceptor is required");

        this.client = client;
        interceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
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
        client.interceptors().add(interceptor);
    }

    @Override
    public Call<MovieResults> movies(@Query("sort_by") String sortBy) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        MovieApiClient movieApiClient = retrofit.create(MovieApiClient.class);

        return movieApiClient.movies(sortBy);
    }
}
