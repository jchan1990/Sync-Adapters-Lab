package drewmahrt.generalassemb.ly.investingportfolio.authenticator;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by Qube on 8/22/16.
 */
public class PlaceholderAuthenticatorService extends Service {
    // Instance field that stores the authenticator object
    private PlaceholderAuthenticator mAuthenticator;
    @Override
    public void onCreate() {
        // Create a new authenticator object
        mAuthenticator = new PlaceholderAuthenticator(this);
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
