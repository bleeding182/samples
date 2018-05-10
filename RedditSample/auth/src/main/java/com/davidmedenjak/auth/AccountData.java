package com.davidmedenjak.auth;

import android.os.Bundle;

public final class AccountData {
    final Bundle bundle = new Bundle();

    private AccountData() {}

    public AccountData and(String key, String value) {
        bundle.putString(key, value);
        return this;
    }

    public static AccountData with(String key, String value) {
        return new AccountData().and(key, value);
    }
}
