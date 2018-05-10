package com.davidmedenjak.auth;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.support.annotation.NonNull;

import com.davidmedenjak.auth.api.model.TokenPair;

import java.io.IOException;

import javax.inject.Inject;

public class OAuthAccountManager implements AccountAuthenticator {

    private final AccountManager accountManager;
    private Account account;

    @Inject
    public OAuthAccountManager(AccountManager accountManager) {
        this.accountManager = accountManager;

        Account[] accounts = accountManager.getAccounts();
        if (accounts.length > 0) {
            account = accounts[0];
        }
    }

    public void login(String accountType, String name, TokenPair token, AccountData accountData) {
        account = new Account(name, accountType);

        final String refreshToken = token.refreshToken;
        if (!accountManager.addAccountExplicitly(account, refreshToken, accountData.bundle)) {
            // account already exists, update refresh token
            accountManager.setPassword(account, refreshToken);
        }

        final String accessToken = token.accessToken;
        accountManager.setAuthToken(account, "bearer", accessToken);
    }

    @Override
    @NonNull
    public String getAccessToken() throws IOException {
        if (account == null) return "";

        try {
            return accountManager.blockingGetAuthToken(account, Token.BEARER, false);
        } catch (OperationCanceledException | AuthenticatorException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    @NonNull
    public String getNewAccessToken(String invalidAccessToken) throws IOException {
        if (account == null) return "";

        accountManager.invalidateAuthToken(account.type, invalidAccessToken);
        return getAccessToken();
    }
}
