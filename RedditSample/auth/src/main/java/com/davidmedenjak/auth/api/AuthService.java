package com.davidmedenjak.auth.api;

import android.content.Intent;

import com.davidmedenjak.auth.api.model.TokenPair;

public interface AuthService {

    /**
     * @return e.g. new Intent(context, LoginActivity.class);
     */
    Intent getLoginIntent();

    void authenticate(String refreshToken, Callback callback);

    interface Callback {
        void onAuthenticated(TokenPair response);
        void onError(Throwable error);
    }
}
