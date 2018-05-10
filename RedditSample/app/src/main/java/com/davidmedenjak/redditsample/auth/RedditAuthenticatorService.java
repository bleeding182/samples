package com.davidmedenjak.redditsample.auth;

import android.accounts.AbstractAccountAuthenticator;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Base64;

import com.davidmedenjak.auth.AuthenticatorService;
import com.davidmedenjak.auth.OAuthAuthenticator;
import com.davidmedenjak.auth.api.AuthService;
import com.davidmedenjak.auth.api.model.TokenPair;
import com.davidmedenjak.redditsample.auth.api.RedditAuthService;
import com.davidmedenjak.redditsample.auth.login.LoginActivity;

import java.nio.charset.Charset;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.moshi.MoshiConverterFactory;

public class RedditAuthenticatorService extends AuthenticatorService {
    @Override
    public AbstractAccountAuthenticator getAuthenticator() {
        Retrofit retrofit = createRetrofit("https://www.reddit.com/api/");
        final RedditAuthService service = retrofit.create(RedditAuthService.class);

        return new OAuthAuthenticator(
                this,
                new AuthService() {
                    @Override
                    public Intent getLoginIntent() {
                        return new Intent(RedditAuthenticatorService.this, LoginActivity.class);
                    }

                    @Override
                    public void authenticate(String refreshToken, Callback callback) {
                        service.authenticate(
                                        getBasicAuthForClientId(), "refresh_token", refreshToken)
                                .subscribe(
                                        tokenResponse ->
                                                callback.onAuthenticated(
                                                        new TokenPair(
                                                                tokenResponse.accessToken,
                                                                tokenResponse.refreshToken)),
                                        callback::onError);
                    }
                });
    }

    private static final String CLIENT_ID = "4tVpFALOLCy1ug";

    @NonNull
    private String getBasicAuthForClientId() {
        byte[] basicAuthBytes = (CLIENT_ID + ":").getBytes();
        byte[] encodedAuthBytes = Base64.encode(basicAuthBytes, Base64.NO_WRAP);
        String clientAuth = new String(encodedAuthBytes, Charset.forName("UTF-8"));
        return "Basic " + clientAuth;
    }

    @NonNull
    private Retrofit createRetrofit(String baseUrl) {
        return new Retrofit.Builder()
                .addConverterFactory(MoshiConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
                .baseUrl(baseUrl)
                .build();
    }
}
