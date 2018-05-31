package com.davidmedenjak.auth.okhttp;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.davidmedenjak.auth.AccountAuthenticator;

import java.io.IOException;

import javax.inject.Inject;

import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

public class RequestRetryAuthenticator implements Authenticator {

    private final AccountAuthenticator authenticator;

    @Inject
    public RequestRetryAuthenticator(AccountAuthenticator authenticator) {
        this.authenticator = authenticator;
    }

    @Nullable
    @Override
    public Request authenticate(@NonNull Route route, @NonNull Response response)
            throws IOException {
        if (response.priorResponse() != null) {
            return null; // Give up, we've already attempted to refresh.
        }

        final String invalidAccessToken = parseHeaderAccessToken(response);

        final String token;
        if (invalidAccessToken.isEmpty()) {
            token = authenticator.getAccessToken();
        } else {
            token = authenticator.getNewAccessToken(invalidAccessToken);
        }

        final String authorization = Headers.AUTH_BEARER + token;

        return response.request()
                .newBuilder()
                .addHeader(Headers.AUTHORIZATION, authorization)
                .build();
    }

    @NonNull
    private String parseHeaderAccessToken(@NonNull Response response) {
        final String invalidAuth = response.request().header(Headers.AUTHORIZATION);
        if (invalidAuth == null) {
            return "";
        }
        return invalidAuth.substring(Headers.AUTH_BEARER.length());
    }
}
