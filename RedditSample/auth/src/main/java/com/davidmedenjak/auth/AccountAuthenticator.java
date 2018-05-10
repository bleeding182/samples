package com.davidmedenjak.auth;

import android.support.annotation.NonNull;

import java.io.IOException;

/** Provides access tokens to use for network requests. */
public interface AccountAuthenticator {
    /**
     * Get an access token for the current user.
     *
     * @return the access token
     * @throws IOException if there was an I/O issue retrieving the token
     */
    @NonNull
    String getAccessToken() throws IOException;

    /**
     * Get a new access token that does not match {@code invalidAccessToken}.
     *
     * @param invalidAccessToken the access token we wish to invalidate
     * @return a new access token != {@code invalidAccessToken}
     * @throws IOException if there was an I/O issue retrieving the token
     */
    @NonNull
    String getNewAccessToken(String invalidAccessToken) throws IOException;
}
