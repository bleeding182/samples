package com.davidmedenjak.auth.okhttp;

import android.support.annotation.NonNull;

import com.davidmedenjak.auth.AccountAuthenticator;

import java.io.IOException;

import javax.inject.Inject;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class RequestAuthInterceptor implements Interceptor {

    private final AccountAuthenticator authenticator;

    @Inject
    public RequestAuthInterceptor(AccountAuthenticator authenticator) {
        this.authenticator = authenticator;
    }

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        final String token = authenticator.getAccessToken();

        if (token.isEmpty()) {
            return chain.proceed(chain.request());
        }
        final Request.Builder requestBuilder = chain.request().newBuilder();

        final String authorization = Headers.AUTH_BEARER + token;
        requestBuilder.addHeader(Headers.AUTHORIZATION, authorization);

        return chain.proceed(requestBuilder.build());
    }
}
