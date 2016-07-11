package com.example.android.sunshine.app.sync;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.os.Bundle;

/**
 * Manages "Authentication" to Sunshine's backend service.  The SyncAdapter framework
 * requires an authenticator object, so syncing to a service that doesn't need authentication
 * typically means creating a stub authenticator like this one.
 * This code is copied directly, in its entirety, from
 * http://developer.android.com/training/sync-adapters/creating-authenticator.html
 * Which is a pretty handy reference when creating your own syncadapters.  Just sayin'.
 */
public class SunshineAuthenticator extends AbstractAccountAuthenticator {

    public SunshineAuthenticator(Context context) {
        super(context);
    }

    // No properties to edit.
    @Override
    public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
        throw new UnsupportedOperationException("No properties to edit");
    }

    // Because we're not actually adding an account to the device, just return null.
    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType, String[] requiredFeatures, Bundle options) throws NetworkErrorException {
        return null;
    }

    // Ignore attempts to confirm credentials
    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account, Bundle options) throws NetworkErrorException {
        return null;
    }

    // Getting an authentication token is not supported
    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
        throw new UnsupportedOperationException("Getting an authentication token is not supported");
    }

    // Getting a label for the auth token is not supported
    @Override
    public String getAuthTokenLabel(String authTokenType) {
        throw new UnsupportedOperationException("Getting a label for the auth token is not supported");
    }

    // Updating user credentials is not supported
    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
        throw new UnsupportedOperationException("Updating user credentials is not supported");
    }

    // Checking features for the account is not supported
    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account, String[] features) throws NetworkErrorException {
        throw new UnsupportedOperationException("Checking features for the account is not supported");
    }
}
