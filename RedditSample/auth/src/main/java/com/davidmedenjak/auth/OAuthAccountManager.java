package com.davidmedenjak.auth;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.davidmedenjak.auth.api.model.TokenPair;

import java.io.IOException;
import java.util.Set;

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
        accountManager.setAuthToken(account, Token.BEARER, accessToken);
    }

    public void logout() {
        if (account == null) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            accountManager.removeAccount(account, null, null, null);
        } else {
            accountManager.removeAccount(account, null, null);
        }
    }

    public boolean isLoggedIn() {
        return account != null;
    }

    @Nullable
    public Account getAccount() {
        return account;
    }

    public void setAccountData(String key, String value) {
        if (!isLoggedIn()) return;

        accountManager.setUserData(account, key, value);
    }

    public void setAccountData(AccountData accountData) {
        if (!isLoggedIn()) return;

        Bundle bundle = accountData.bundle;
        Set<String> keySet = bundle.keySet();
        for (String key : keySet) {
            accountManager.setUserData(account, key, bundle.getString(key));
        }
    }

    @NonNull
    public String getAccountData(String key) {
        if (!isLoggedIn()) return "";

        String data = accountManager.getUserData(account, key);

        if (data == null) return "";
        return data;
    }

    @Override
    @NonNull
    public String getAccessToken() throws IOException {
        if (!isLoggedIn()) return "";

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
        if (!isLoggedIn()) return "";

        accountManager.invalidateAuthToken(account.type, invalidAccessToken);
        return getAccessToken();
    }
}
