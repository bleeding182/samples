package com.davidmedenjak.redditsample.auth.authenticator;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

// https://developer.android.com/training/sync-adapters/creating-authenticator.html
public class RedditAuthenticatorService extends Service {

    private static final String TAG = "RedditAuthService";

    /** Instance field that stores the authenticator object */
    private RedditAuthenticator authenticator;

    @Override
    public void onCreate() {
        // Create a new authenticator object
        Log.v(TAG, "RedditAuthenticatorService created");
        authenticator = new RedditAuthenticator(this);
    }

    /*
     * When the system binds to this Service to make the RPC call
     * return the authenticator's IBinder.
     */
    @Override
    public IBinder onBind(Intent intent) {
        Log.v(TAG, "onBind " + intent.toString());
        return authenticator.getIBinder();
    }
}
