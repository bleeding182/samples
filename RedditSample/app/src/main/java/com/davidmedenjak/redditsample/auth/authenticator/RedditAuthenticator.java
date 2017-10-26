package com.davidmedenjak.redditsample.auth.authenticator;

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
import android.util.Log;

import com.davidmedenjak.redditsample.auth.login.LoginActivity;
import com.davidmedenjak.redditsample.common.util.BundleUtil;

import java.util.Arrays;

public class RedditAuthenticator extends AbstractAccountAuthenticator {

    private static final String TAG = "RedditAuthenticator";
    private Context context;

    public RedditAuthenticator(Context context) {
        super(context);
        Log.v(TAG, "RedditAuthenticator created");
        this.context = context;
    }

    @Override
    public Bundle editProperties(
            @NonNull AccountAuthenticatorResponse response, @NonNull String accountType) {
        Log.d(TAG, "editProperties for " + accountType);
        return null;
    }

    // add account: addAccount for com.davidmedenjak.redditsample as null with features null and
    // options {"androidPackageName":"com.android.settings", "callerPid":"4997", "callerUid":"1000",
    // "pendingIntent":"PendingIntent{70aed3e: android.os.BinderProxy@fe4179f}",
    // "hasMultipleUsers":"false"}

    @Override
    public Bundle addAccount(
            @NonNull AccountAuthenticatorResponse response,
            @NonNull String accountType,
            @Nullable String authTokenType,
            @Nullable String[] requiredFeatures,
            @Nullable Bundle options)
            throws NetworkErrorException {
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


        // can't directly start a webview
        // java.lang.SecurityException: KEY_INTENT resolved to an Activity (com.google.android.apps.chrome.Main) in a package (com.android.chrome) that does not share a signature with the supplying authenticator (com.davidmedenjak.redditsample).
        //     at com.android.server.accounts.AccountManagerService$Session.checkKeyIntent(AccountManagerService.java:4603)
        // at com.android.server.accounts.AccountManagerService$Session.onResult(AccountManagerService.java:4755)
        // at android.accounts.IAccountAuthenticatorResponse$Stub.onTransact(IAccountAuthenticatorResponse.java:59)
        // at android.os.Binder.execTransact(Binder.java:674)

        final Intent intent = new Intent(context, LoginActivity.class);
//        intent.putExtra(LoginActivity.ARG_ACCOUNT_TYPE, accountType);
//        intent.putExtra(LoginActivity.ARG_AUTH_TYPE, authTokenType);
//        intent.putExtra(LoginActivity.ARG_IS_ADDING_NEW_ACCOUNT, true);
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
            @NonNull AccountAuthenticatorResponse response,
            @NonNull Account account,
            @NonNull String authTokenType,
            @Nullable Bundle options)
            throws NetworkErrorException {
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
        return null;
    }

    @Override
    public String getAuthTokenLabel(@NonNull String authTokenType) {
        Log.d(TAG, "getAuthTokenLabel for " + authTokenType);
        return authTokenType;
    }

    @Override
    public Bundle updateCredentials(
            @NonNull AccountAuthenticatorResponse response,
            @NonNull Account account,
            @Nullable String authTokenType,
            @Nullable Bundle options)
            throws NetworkErrorException {
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
}
