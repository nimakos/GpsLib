package gr.nikolis.gpslib.gps.engines;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;

import java.lang.ref.WeakReference;

public final class AndroidEngine implements LocationListener {

    public interface OnAndroidListener {
        void getAndroidLocation(Location location);
    }

    //Required
    private OnAndroidListener onAndroidListener;

    //Optional
    private final long myMinTime, myMinDistance;

    //class parameters
    private LocationManager locationManager;
    private static AndroidEngine INSTANCE;

    public static class Builder {
        //Required
        private final Context context;
        private final OnAndroidListener onAndroidListener;

        //optional parameters
        private long minimumTime = 1000;
        private long minimumDistance = 0;
        private boolean createSingleInstance;

        public Builder(Context context, OnAndroidListener onAndroidListener) {
            this.context = context;
            this.onAndroidListener = onAndroidListener;
        }

        public Builder setMinimumDistance(long distance) {
            this.minimumDistance = distance;
            return this;
        }

        public Builder setMinimumTime(long time) {
            this.minimumTime = time;
            return this;
        }

        public Builder hasSingleInstance(boolean createSingleInstance) {
            this.createSingleInstance = createSingleInstance;
            return this;
        }

        public AndroidEngine build() {
            return getInstance(this);
        }
    }

    /**
     * The private main class constructor
     *
     * @param builder The builder class
     */
    private AndroidEngine(Builder builder) {
        WeakReference<Context> contextWeakReference = new WeakReference<>(builder.context);
        this.onAndroidListener = builder.onAndroidListener;
        this.myMinDistance = builder.minimumDistance;
        this.myMinTime = builder.minimumTime;
        init(contextWeakReference.get());
    }

    private synchronized static AndroidEngine getInstance(Builder builder) {
        if (builder.createSingleInstance) {
            if (INSTANCE == null) {
                synchronized (AndroidEngine.class) {
                    INSTANCE = new AndroidEngine(builder);
                }
            } else {
                return INSTANCE;
            }
        } else {
            synchronized (AndroidEngine.class) {
                INSTANCE = new AndroidEngine(builder);
            }
        }
        return INSTANCE;
    }

    @SuppressLint("MissingPermission")
    private void init(Context context) {
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        if (locationManager != null)
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, myMinTime, myMinDistance, this);
    }


    /**
     * This destructor destroys all instances and removes location updates
     */
    @SuppressLint("MissingPermission")
    public void destroyInstance() {
        locationManager.removeUpdates(this);
        onAndroidListener = null;
        locationManager = null;
    }


    @Override
    public void onLocationChanged(@NonNull Location location) {
       if (onAndroidListener != null)
           onAndroidListener.getAndroidLocation(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }
}
