package com.davidmedenjak.auth.okhttp;

import android.support.annotation.NonNull;

import com.davidmedenjak.auth.AccountAuthenticator;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class RequestAuthInterceptor implements Interceptor {
    private final AccountAuthenticator authenticator;

    public RequestAuthInterceptor(AccountAuthenticator authenticator) {
        this.authenticator = authenticator;
    }

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        String token = authenticator.getAccessToken();

        if (token.isEmpty()) {
            return chain.proceed(chain.request());
        }

        final String authorization = Header.AUTH_BEARER + token;

        Request request =
                chain.request().newBuilder().addHeader(Header.AUTHORIZATION, authorization).build();

        return chain.proceed(request);
    }
}
