package com.davidmedenjak.redditsample.features.latestcomments;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Context;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

class AuthInterceptor implements Interceptor {
    private final AccountManager accountManager;
    private Account account;

    public AuthInterceptor(Account account, Context context) {
        this.account = account;
        accountManager = AccountManager.get(context);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {

        String token = "";
        try {
            token = accountManager.blockingGetAuthToken(account, "bearer", false);
        } catch (OperationCanceledException e) {
            e.printStackTrace();
        } catch (AuthenticatorException e) {
            e.printStackTrace();
        }

        final String authorization = "Bearer " + token;

        Request request =
                chain.request().newBuilder().addHeader("Authorization", authorization).build();

        return chain.proceed(request);
    }
}
