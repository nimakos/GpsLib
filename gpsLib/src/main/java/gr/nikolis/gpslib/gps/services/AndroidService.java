package gr.nikolis.gpslib.gps.services;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;

import gr.nikolis.gpslib.gps.engines.AndroidEngine;
import gr.nikolis.gpslib.receiver.MyBroadcastReceiver;

public class AndroidService extends Service implements AndroidEngine.OnAndroidListener {

    private AndroidEngine androidEngine;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        androidEngine = new AndroidEngine.Builder(this, this)
                .setMinimumTime(1000)
                .setMinimumDistance(0)
                .hasSingleInstance(true)
                .build();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (androidEngine != null) {
            androidEngine.destroyInstance();
            androidEngine = null;
        }
    }

    @Override
    public void getAndroidLocation(Location location) {
        Intent i = new Intent(MyBroadcastReceiver.LOCATION_UPDATE);
        i.putExtra(MyBroadcastReceiver.COORDINATES, location);
        sendBroadcast(i);
    }
}
