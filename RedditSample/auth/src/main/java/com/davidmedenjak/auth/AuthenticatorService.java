package com.davidmedenjak.auth;

import android.accounts.AbstractAccountAuthenticator;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public abstract class AuthenticatorService extends Service {

    private static final String TAG = "AuthenticatorService";

    private AbstractAccountAuthenticator authenticator;

    @Override
    public void onCreate() {
        // Create a new authenticator object
        Log.v(TAG, "AuthenticatorService created");
        authenticator = getAuthenticator();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.v(TAG, "onBind " + intent.toString());
        return authenticator.getIBinder();
    }

    public abstract AbstractAccountAuthenticator getAuthenticator();
}
