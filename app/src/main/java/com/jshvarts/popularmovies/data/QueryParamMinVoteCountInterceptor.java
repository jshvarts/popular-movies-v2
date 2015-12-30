package com.jshvarts.popularmovies.data;

import com.jshvarts.popularmovies.BuildConfig;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * Global API Key interceptor required to append apiKey for all movieDB requests.
 */
public class QueryParamMinVoteCountInterceptor implements Interceptor {
    private final String MIN_VOTE_COUNT_PARAM = "vote_count.gte";
    private final String MIN_VOTE_COUNT_PARAM_VALUE = "1000";

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        HttpUrl url = request.httpUrl().newBuilder()
                .addQueryParameter(MIN_VOTE_COUNT_PARAM, MIN_VOTE_COUNT_PARAM_VALUE)
                .build();
        request = request.newBuilder().url(url).build();
        return chain.proceed(request);
    }
}
