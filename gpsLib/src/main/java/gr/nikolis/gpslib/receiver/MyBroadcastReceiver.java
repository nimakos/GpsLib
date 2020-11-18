package gr.nikolis.gpslib.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;

import androidx.annotation.NonNull;

public final class MyBroadcastReceiver extends BroadcastReceiver {

    public static final String LOCATION_UPDATE = "location_update";
    public static final String SUCCESS_UPDATE = "success_update";
    public static final String SPEED_UPDATE = "speed_update";
    public static final String COORDINATES = "coordinates";
    public static final String SPEED = "speed";
    public static final String INIT_GPS_STATUS = "init_gps_status";

    public interface OnLocationUpdateListener {
        void getBroadcastLocationUpdate(Location location);
    }

    public interface OnSuccessUpdateListener {
        void getBroadcastSuccessLocationUpdate(Location location);
    }

    public interface OnSpeedUpdateListener {
        void getBroadcastSpeedUpdate(float speed);
    }

    public interface OnGpsStatusChangedListener {
        void getBroadcastGpsStatusUpdate(boolean isGpsEnabled);
    }

    private final OnLocationUpdateListener onLocationUpdateListener;
    private final OnSuccessUpdateListener onSuccessListener;
    private final OnSpeedUpdateListener onSpeedUpdateListener;
    private final OnGpsStatusChangedListener onGpsStatusChangedListener;

    private MyBroadcastReceiver(@NonNull Builder builder) {
        this.onLocationUpdateListener = builder.onLocationUpdateListener;
        this.onSuccessListener = builder.onSuccessListener;
        this.onSpeedUpdateListener = builder.onSpeedUpdateListener;
        this.onGpsStatusChangedListener = builder.onGpsStatusChangedListener;
    }

    public static class Builder {
        private OnLocationUpdateListener onLocationUpdateListener;
        private OnSuccessUpdateListener onSuccessListener;
        private OnSpeedUpdateListener onSpeedUpdateListener;
        private OnGpsStatusChangedListener onGpsStatusChangedListener;

        public Builder() {
        }

        public Builder setLocationUpdate(OnLocationUpdateListener locationUpdate) {
            this.onLocationUpdateListener = locationUpdate;
            return this;
        }

        public Builder setSuccessLocationUpdate(OnSuccessUpdateListener successLocationUpdate) {
            this.onSuccessListener = successLocationUpdate;
            return this;
        }

        public Builder setSpeedUpdate(OnSpeedUpdateListener speedUpdate) {
            this.onSpeedUpdateListener = speedUpdate;
            return this;
        }

        public Builder setGpsUpdate(OnGpsStatusChangedListener gpsStatusUpdate) {
            this.onGpsStatusChangedListener = gpsStatusUpdate;
            return this;
        }

        public MyBroadcastReceiver build() {
            return new MyBroadcastReceiver(this);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action != null) {
            if (action.equals(LOCATION_UPDATE)) {
                if (intent.getExtras() != null) {
                    Location location = (Location) intent.getExtras().get(COORDINATES);
                    if (onLocationUpdateListener != null && location != null) {
                        onLocationUpdateListener.getBroadcastLocationUpdate(location);
                    }
                }
            }
            if (action.equals(SUCCESS_UPDATE)) {
                if (intent.getExtras() != null) {
                    Location location = (Location) intent.getExtras().get(COORDINATES);
                    if (onSuccessListener != null && location != null) {
                        onSuccessListener.getBroadcastSuccessLocationUpdate(location);
                    }
                }
            }
            if (action.equals(SPEED_UPDATE)) {
                if (intent.getExtras() != null) {
                    float speed = (float) intent.getExtras().get(SPEED);
                    if (onSpeedUpdateListener != null) {
                        onSpeedUpdateListener.getBroadcastSpeedUpdate(speed);
                    }
                }
            }
            if (action.equals(LocationManager.PROVIDERS_CHANGED_ACTION)) {
                String providerName;
                if (intent.getExtras() != null && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                    providerName = (String) intent.getExtras().get(LocationManager.EXTRA_PROVIDER_NAME);
                    if (providerName != null && providerName.equals(LocationManager.GPS_PROVIDER)) {
                        if (onGpsStatusChangedListener != null) {
                            onGpsStatusChangedListener.getBroadcastGpsStatusUpdate(isGpsEnabled(context));
                        }
                    }
                } else {
                    if (onGpsStatusChangedListener != null) {
                        onGpsStatusChangedListener.getBroadcastGpsStatusUpdate(isGpsEnabled(context));
                    }
                }
            }
            if (action.equals(INIT_GPS_STATUS)) {
                if (onGpsStatusChangedListener != null) {
                    onGpsStatusChangedListener.getBroadcastGpsStatusUpdate(isGpsEnabled(context));
                }
            }
        }
    }

    /**
     * Check the Gps status
     *
     * @param context The app context
     * @return True if gps is enabled, false otherWay
     */
    private boolean isGpsEnabled(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } else {
            return false;
        }
    }
}