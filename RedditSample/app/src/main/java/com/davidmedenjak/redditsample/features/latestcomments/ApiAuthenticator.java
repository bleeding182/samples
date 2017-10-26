package com.davidmedenjak.redditsample.features.latestcomments;


import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;

import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

public class ApiAuthenticator implements Authenticator {

    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String AUTHORIZATION_PREFIX = "Bearer ";

    private static final String TAG = "ApiAuthenticator";

    private Account account;
    private AccountManager accountManager;

    public ApiAuthenticator(Account account, AccountManager accountManager) {
        this.account = account;
        this.accountManager = accountManager;
    }

    @Nullable
    @Override
    public Request authenticate(@NonNull Route route, @NonNull Response response) throws IOException {
        if (triedRefreshingToken(response)) {
            Log.i(TAG, "Auth failed with new token, don't retry again.");
            return null;
        }
        final Request request = response.request();

        Log.i(TAG, "Auth error, retry with new token...");
        invalidateCurrentAccessToken(request);
        final String newAccessToken = queryNewAccessToken();

        if (newAccessToken == null) {
            Log.w(TAG, "Retrieving a new token failed");
            return null;
        }

        Log.d(TAG, "New token " + newAccessToken + ", retrying...");
        return updateRequestWithNewToken(request, newAccessToken);
    }

    private boolean triedRefreshingToken(@NonNull Response response) {
        return responseCount(response) > 1;
    }

    private int responseCount(Response response) {
        Response temp = response;
        int result = 0;
        while (temp != null) {
            temp = temp.priorResponse();
            result++;
        }
        return result;
    }

    private void invalidateCurrentAccessToken(Request request) {
        String authorization = request.header(HEADER_AUTHORIZATION);
        String accessToken = authorization.substring(AUTHORIZATION_PREFIX.length());

        Log.d(TAG, "Invalidating last token " + accessToken);
        accountManager.invalidateAuthToken(account.type, accessToken);
    }

    @Nullable
    private String queryNewAccessToken() throws IOException {
        try {
            return accountManager.blockingGetAuthToken(account, "bearer", false);
        } catch (OperationCanceledException | AuthenticatorException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Request updateRequestWithNewToken(Request request, String newAccessToken) {
        return request.newBuilder()
                .removeHeader(HEADER_AUTHORIZATION)
                .addHeader(HEADER_AUTHORIZATION, AUTHORIZATION_PREFIX + newAccessToken)
                .build();
    }
}
