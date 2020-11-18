package gr.nikolis.gpslib.gps.services;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;

import androidx.annotation.NonNull;

import gr.nikolis.gpslib.gps.engines.MapBoxEngine;
import gr.nikolis.gpslib.receiver.MyBroadcastReceiver;

import static gr.nikolis.gpslib.Common.DISPLACEMENT;
import static gr.nikolis.gpslib.Common.FASTEST_INTERVAL;
import static gr.nikolis.gpslib.Common.INTERVAL;
import static gr.nikolis.gpslib.Common.MAX_WAIT_TIME;
import static gr.nikolis.gpslib.Common.PRIORITY;

public class MapBoxService extends Service implements MapBoxEngine.OnLocationUpdateListener {

    private MapBoxEngine mapBoxEngine;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        mapBoxEngine = new MapBoxEngine
                .Builder(this, this)
                .hasSingleInstance(true)
                .setPriority(PRIORITY)
                .setInterval(INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL)
                .setMaxWaitTime(MAX_WAIT_TIME)
                .setDisplacement(DISPLACEMENT)
                .build();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mapBoxEngine != null) {
            mapBoxEngine.destroyInstance();
            mapBoxEngine = null;
        }
    }

    @Override
    public void onSuccessLocationUpdate(Location location) {
        Intent i = new Intent(MyBroadcastReceiver.LOCATION_UPDATE);
        i.putExtra(MyBroadcastReceiver.COORDINATES, location);
        sendBroadcast(i);
    }

    @Override
    public void onFailureLocationUpdate(@NonNull Exception exception) {
    }
}