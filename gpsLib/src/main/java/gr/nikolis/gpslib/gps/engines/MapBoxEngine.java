package gr.nikolis.gpslib.gps.engines;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.location.LocationEngineResult;

import java.lang.ref.WeakReference;

public final class MapBoxEngine implements LocationEngineCallback<LocationEngineResult> {

    public interface OnLocationUpdateListener {
        void onSuccessLocationUpdate(Location location);

        void onFailureLocationUpdate(@NonNull Exception exception);
    }

    //required parameters
    private OnLocationUpdateListener onLocationUpdateListener;

    //optional parameters
    private final long maxWaitTime, interval, fastestInterval;
    private final int priority;
    private final float displacement;

    //class parameters
    private static MapBoxEngine INSTANCE;
    private LocationEngine locationEngine;

    public static class Builder {
        //required parameters
        private final WeakReference<Context> contextWeakReference;
        private final OnLocationUpdateListener onLocationUpdateListener;

        //optional parameters
        private long maxWaitTime = 0L, interval = 0L, fastestInterval = 1000L;
        private int priority = LocationEngineRequest.PRIORITY_HIGH_ACCURACY;
        private float displacement = 0.0f;
        private boolean createSingleInstance;

        /**
         * The Builder constructor
         *
         * @param context The Activity context
         */
        public Builder(Context context, OnLocationUpdateListener onLocationUpdateListener) {
            contextWeakReference = new WeakReference<>(context);
            this.onLocationUpdateListener = onLocationUpdateListener;
        }

        public Builder setMaxWaitTime(long maxWaitTime) {
            this.maxWaitTime = maxWaitTime;
            return this;
        }

        public Builder setInterval(long interval) {
            this.interval = interval;
            return this;
        }

        public Builder setFastestInterval(long fastestInterval) {
            this.fastestInterval = fastestInterval;
            return this;
        }

        public Builder setDisplacement(float displacement) {
            this.displacement = displacement;
            return this;
        }

        public Builder setPriority(int priority) {
            this.priority = priority;
            return this;
        }

        public Builder hasSingleInstance(boolean createSingleInstance) {
            this.createSingleInstance = createSingleInstance;
            return this;
        }

        public MapBoxEngine build() {
            return getInstance(this);
        }
    }

    /**
     * The private main class constructor
     *
     * @param builder The builder class
     */
    private MapBoxEngine(@NonNull Builder builder) {
        this.onLocationUpdateListener = builder.onLocationUpdateListener;
        this.maxWaitTime = builder.maxWaitTime;
        this.interval = builder.interval;
        this.priority = builder.priority;
        this.displacement = builder.displacement;
        this.fastestInterval = builder.fastestInterval;
        init(builder.contextWeakReference.get());
    }

    /**
     * Singleton pattern under circumstances.
     * This constructor creates only one instance, if we choose to
     *
     * @param builder The Builder class
     * @return This single instance
     */
    private synchronized static MapBoxEngine getInstance(@NonNull Builder builder) {
        if (builder.createSingleInstance) {
            if (INSTANCE == null) {
                synchronized (MapBoxEngine.class) {
                    INSTANCE = new MapBoxEngine(builder);
                }
            } else {
                return INSTANCE;
            }
        } else {
            synchronized (MapBoxEngine.class) {
                INSTANCE = new MapBoxEngine(builder);
            }
        }
        return INSTANCE;
    }

    /**
     * This destructor destroys all instances and removes location updates
     */
    public void destroyInstance() {
        locationEngine.removeLocationUpdates(this);
        onLocationUpdateListener = null;
        INSTANCE = null;
    }

    /**
     * initialize location engine provider
     *
     * @param context The activity context
     */
    @SuppressLint("MissingPermission")
    private void init(Context context) {
        locationEngine = LocationEngineProvider.getBestLocationEngine(context);
        LocationEngineRequest request = new LocationEngineRequest
                .Builder(interval)
                .setPriority(priority)
                .setMaxWaitTime(maxWaitTime)
                .setDisplacement(displacement)
                .setFastestInterval(fastestInterval)
                .build();
        locationEngine.requestLocationUpdates(request, this, Looper.myLooper());
    }

    @Override
    public void onSuccess(LocationEngineResult result) {
        if (result.getLastLocation() != null && onLocationUpdateListener != null)
            onLocationUpdateListener.onSuccessLocationUpdate(result.getLastLocation());
    }

    @Override
    public void onFailure(@NonNull Exception exception) {
        if (onLocationUpdateListener != null)
            onLocationUpdateListener.onFailureLocationUpdate(exception);
    }
}