package gr.nikolis.gpslib;

import com.mapbox.android.core.location.LocationEngineRequest;

public class Common {
    //GPS
    public static final float MPS_to_KPH = 3.6f;
    public static final int PRIORITY = LocationEngineRequest.PRIORITY_HIGH_ACCURACY;
    public static final long INTERVAL = 1000L;
    public static final long FASTEST_INTERVAL = 1000L;
    public static final long MAX_WAIT_TIME = 0L;
    public static final float DISPLACEMENT = 0.0f; // -> distance in meters between hits
}