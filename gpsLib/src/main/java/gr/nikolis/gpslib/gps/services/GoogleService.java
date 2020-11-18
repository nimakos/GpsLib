package gr.nikolis.gpslib.gps.services;


import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;

import gr.nikolis.gpslib.gps.engines.GoogleEngine;
import gr.nikolis.gpslib.receiver.MyBroadcastReceiver;

public class GoogleService extends Service implements GoogleEngine.OnLocationUpdateListener {

    private GoogleEngine googleEngine;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        googleEngine = new GoogleEngine.Builder(this, this)
                .setUpdateInterval(1000)
                .setFastestInterval(1)
                .hasSingleInstance(true)
                .build();
    }

    @Override
    public void getGoogleLocationUpdate(Location location) {
        Intent i = new Intent(MyBroadcastReceiver.LOCATION_UPDATE);
        i.putExtra(MyBroadcastReceiver.COORDINATES, location);
        sendBroadcast(i);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (googleEngine != null) {
            googleEngine.destroyInstance();
            googleEngine = null;
        }
    }
}
