package com.davidmedenjak.auth;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.davidmedenjak.auth.api.AuthService;
import com.davidmedenjak.auth.api.model.TokenPair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

@SuppressWarnings("unused")
public class OAuthAuthenticator extends AbstractAccountAuthenticator {

    private boolean fetchingToken;
    private List<AccountAuthenticatorResponse> queue = null;

    private static final String TAG = "OAuthAuthenticator";
    private final Context context;
    private final AuthService service;


    private boolean loggingEnabled = false;

    @Inject
    public OAuthAuthenticator(Context context, AuthService service) {
        super(context);
        if (loggingEnabled) Log.v(TAG, "OAuthAuthenticator created");
        this.service = service;
        this.context = context;
    }

    @Override
    public Bundle editProperties(
            @NonNull AccountAuthenticatorResponse response, @NonNull String accountType) {
        if (loggingEnabled) Log.d(TAG, "editProperties for " + accountType);
        return null;
    }

    @Override
    public Bundle addAccount(
            @NonNull AccountAuthenticatorResponse response,
            @NonNull String accountType,
            @Nullable String authTokenType,
            @Nullable String[] requiredFeatures,
            @Nullable Bundle options)
            throws NetworkErrorException {
        if (loggingEnabled)
            Log.d(
                    TAG,
                    "addAccount for "
                            + accountType
                            + " as "
                            + authTokenType
                            + " with features "
                            + Arrays.toString(requiredFeatures)
                            + " and options "
                            + BundleUtil.toString(options));

        final Intent intent = service.getLoginIntent();
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);

        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);

        return bundle;
    }

    @Override
    public Bundle confirmCredentials(
            @NonNull AccountAuthenticatorResponse response,
            @NonNull Account account,
            @Nullable Bundle options)
            throws NetworkErrorException {
        if (loggingEnabled)
            Log.d(
                    TAG,
                    "confirmCredentials for "
                            + account.type
                            + ":"
                            + account.name
                            + " with options "
                            + BundleUtil.toString(options));
        return null;
    }

    @Override
    public Bundle getAuthToken(
            @NonNull final AccountAuthenticatorResponse response,
            @NonNull final Account account,
            @NonNull final String authTokenType,
            @Nullable final Bundle options)
            throws NetworkErrorException {
        if (loggingEnabled)
            Log.d(
                    TAG,
                    "getAuthToken for "
                            + account.type
                            + ":"
                            + account.name
                            + " as "
                            + authTokenType
                            + " with options "
                            + BundleUtil.toString(options));

        if (isAnotherThreadHandlingIt(response)) return null;

        final AccountManager accountManager = AccountManager.get(context);

        final String authToken = accountManager.peekAuthToken(account, authTokenType);

        if (TextUtils.isEmpty(authToken)) {
            synchronized (this) {
                // queue as well
                isAnotherThreadHandlingIt(response);
            }
            refreshToken(account, accountManager, authTokenType);
        } else {
            Bundle resultBundle = createResultBundle(account, authToken);
            returnResultToWaiting(resultBundle);
            return resultBundle;
        }

        // return result via response async
        return null;
    }

    private boolean isAnotherThreadHandlingIt(@NonNull AccountAuthenticatorResponse response) {
        synchronized (this) {
            if (fetchingToken) {
                // another thread is already working on it, register for callback
                List<AccountAuthenticatorResponse> q = queue;
                if (q == null) {
                    q = new ArrayList<>();
                    queue = q;
                }
                q.add(response);
                // we return null, the result will be sent with the `response`
                return true;
            }
            // we have to fetch the token, and return the result other threads
            fetchingToken = true;
        }
        return false;
    }

    private void returnResultToWaiting(Bundle result) {
        for (; ; ) {
            List<AccountAuthenticatorResponse> q;
            synchronized (this) {
                q = queue;
                if (q == null) {
                    fetchingToken = false;
                    return;
                }
                queue = null;
            }
            for (AccountAuthenticatorResponse r : q) {
                r.onResult(result);
            }
        }
    }

    private void returnErrorToWaiting(int error, String message) {
        for (; ; ) {
            List<AccountAuthenticatorResponse> q;
            synchronized (this) {
                q = queue;
                if (q == null) {
                    fetchingToken = false;
                    return;
                }
                queue = null;
            }
            for (AccountAuthenticatorResponse r : q) {
                r.onError(error, message);
            }
        }
    }

    private void refreshToken(@NonNull Account account, AccountManager accountManager, String authTokenType) {
        final String refreshToken = accountManager.getPassword(account);

        service.authenticate(
                refreshToken,
                new AuthService.Callback() {
                    @Override
                    public void onAuthenticated(TokenPair tokenPair) {
                        accountManager.setPassword(account, tokenPair.refreshToken);
                        accountManager.setAuthToken(account, authTokenType, tokenPair.accessToken);

                        Bundle bundle = createResultBundle(account, tokenPair.accessToken);
                        returnResultToWaiting(bundle);
                    }

                    @Override
                    public void onError(Throwable error) {
                        returnErrorToWaiting(
                                AccountManager.ERROR_CODE_NETWORK_ERROR, error.getMessage());
                    }
                });
    }

    @NonNull
    private Bundle createResultBundle(@NonNull Account account, String authToken) {
        final Bundle result = new Bundle();
        result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
        result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
        result.putString(AccountManager.KEY_AUTHTOKEN, authToken);
        return result;
    }

    @Override
    public String getAuthTokenLabel(@NonNull String authTokenType) {
        if (loggingEnabled) Log.d(TAG, "getAuthTokenLabel for " + authTokenType);
        return authTokenType;
    }

    @Override
    public Bundle updateCredentials(
            @NonNull AccountAuthenticatorResponse response,
            @NonNull Account account,
            @Nullable String authTokenType,
            @Nullable Bundle options)
            throws NetworkErrorException {
        if (loggingEnabled)
            Log.d(
                    TAG,
                    "updateCredentials for "
                            + account.type
                            + ":"
                            + account.name
                            + " as "
                            + authTokenType
                            + " with options "
                            + BundleUtil.toString(options));
        return null;
    }

    @Override
    public Bundle hasFeatures(
            @NonNull AccountAuthenticatorResponse response,
            @NonNull Account account,
            @NonNull String[] features)
            throws NetworkErrorException {
        if (loggingEnabled)
            Log.d(
                    TAG,
                    "hasFeatures for "
                            + account.type
                            + ":"
                            + account.name
                            + " for features "
                            + Arrays.toString(features));
        return null;
    }

    public boolean isLoggingEnabled() {
        return loggingEnabled;
    }

    public void setLoggingEnabled(boolean loggingEnabled) {
        this.loggingEnabled = loggingEnabled;
    }
}
