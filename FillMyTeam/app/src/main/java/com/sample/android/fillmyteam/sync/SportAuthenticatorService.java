package com.sample.android.fillmyteam.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Sync service
 */
public class SportAuthenticatorService extends Service {

    // Instance field that stores the authenticator object
    private SportsAuthenticator mAuthenticator;
    @Override
    public void onCreate() {

        mAuthenticator = new SportsAuthenticator(this);
    }
    /*
     * When the system binds to this Service to make the RPC call
     * return the authenticator's IBinder.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
